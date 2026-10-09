package engine

import spaceObjects.GravityObject
import spaceObjects.Vec2
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.cbrt
import units.Kilogram
import units.Kilometer
import units.Quantity

/**
 * Single-algorithm (Barnes–Hut) N-body engine.
 *
 * Physical state lives in flat [DoubleArray]s; [bodies] is the parallel source list used for
 * rendering and identity. INVARIANT: [bodies] and the internal arrays are always kept in sync
 * by [rebuild] (full resync) and by the engine's own collision/escape handling. Never
 * add/remove/reorder [bodies] directly after construction — do it through [rebuild] — or the
 * array/list mapping will desync.
 *
 * Bodies that leave the fixed `[-MAP_HALF_KM, MAP_HALF_KM]^2` map are removed from the
 * simulation by [step]. Gravity is approximated with [theta] (Barnes–Hut monopole threshold),
 * so Newton's third law is not enforced exactly; momentum drifts slightly on large systems.
 */
class PhysicsEngine(
    val bodies: MutableList<GravityObject>,
    initialCapacity: Int = bodies.size,
    private val theta: Double = BARNES_HUT_THETA,
    threads: Int = Runtime.getRuntime().availableProcessors(),
) {
    private val threadCount = threads.coerceAtLeast(1)
    private var capacity = initialCapacity.coerceAtLeast(0)
    private var posX = DoubleArray(capacity)
    private var posY = DoubleArray(capacity)
    private var velX = DoubleArray(capacity)
    private var velY = DoubleArray(capacity)
    private var mass = DoubleArray(capacity)
    private var radius = DoubleArray(capacity)
    private var accX = DoubleArray(capacity)
    private var accY = DoubleArray(capacity)

    private val barnesHut = BarnesHut()

    private var pool: ExecutorService? = null

    var count: Int = 0
        private set

    private var collided = false

    fun rebuild() {
        count = bodies.size
        ensureCapacity(count)
        for (i in 0 until count) {
            val body = bodies[i]
            posX[i] = body.position.x
            posY[i] = body.position.y
            velX[i] = body.velocity.x
            velY[i] = body.velocity.y
            mass[i] = body.mass.value
            radius[i] = body.radius.value
        }
    }

    private fun ensureCapacity(required: Int) {
        if (required <= capacity) return
        var newCapacity = if (capacity <= 0) 16 else capacity
        while (newCapacity < required) newCapacity *= 2

        posX = posX.copyOf(newCapacity)
        posY = posY.copyOf(newCapacity)
        velX = velX.copyOf(newCapacity)
        velY = velY.copyOf(newCapacity)
        mass = mass.copyOf(newCapacity)
        radius = radius.copyOf(newCapacity)
        accX = accX.copyOf(newCapacity)
        accY = accY.copyOf(newCapacity)
        capacity = newCapacity
    }

    fun step(dt: Double) {
        if (count == 0 || dt <= 0.0) return

        barnesHut.build(posX, posY, mass, radius, count)

        if (threadCount <= 1 || count < BARNES_HUT_PARALLEL_MIN_BODIES) {
            barnesHut.computeRange(posX, posY, mass, radius, accX, accY, 0, count, theta)
        } else {
            parallelTraverse()
        }

        integrate(dt)
        removeEscaped()
    }

    private fun parallelTraverse() {
        val usedThreads = minOf(threadCount, count)
        val block = (count + usedThreads - 1) / usedThreads

        val tasks = ArrayList<Callable<Unit>>(usedThreads - 1)
        for (t in 0 until usedThreads - 1) {
            val start = t * block
            val end = minOf(start + block, count)
            tasks += Callable {
                barnesHut.computeRange(posX, posY, mass, radius, accX, accY, start, end, theta)
            }
        }

        val inlineStart = minOf((usedThreads - 1) * block, count)
        barnesHut.computeRange(posX, posY, mass, radius, accX, accY, inlineStart, count, theta)

        pool().invokeAll(tasks)
    }

    private fun pool(): ExecutorService {
        pool?.let { return it }
        val created = Executors.newFixedThreadPool(threadCount) { runnable ->
            Thread(runnable, "gravsim-physics").apply { isDaemon = true }
        }
        pool = created
        return created
    }

    private fun integrate(dt: Double) {
        val px = posX
        val py = posY
        val vx = velX
        val vy = velY
        val ax = accX
        val ay = accY

        for (i in 0 until count) {
            val newVx = vx[i] + ax[i] * dt
            val newVy = vy[i] + ay[i] * dt
            vx[i] = newVx
            vy[i] = newVy
            px[i] += newVx * dt
            py[i] += newVy * dt
        }
    }

    private fun removeEscaped() {
        var i = 0
        while (i < count) {
            if (abs(posX[i]) > MAP_HALF_KM || abs(posY[i]) > MAP_HALF_KM) {
                removeAt(i)
            } else {
                i++
            }
        }
    }

    fun resolveCollisions() {
        var i = 0
        while (i < count) {
            var j = i + 1
            var merged = false
            while (j < count) {
                val dx = posX[j] - posX[i]
                val dy = posY[j] - posY[i]
                val contact = radius[i] + radius[j]

                if (dx * dx + dy * dy <= contact * contact) {
                    val primary = if (mass[i] >= mass[j]) i else j
                    val absorbed = if (primary == i) j else i
                    mergeInto(primary, absorbed)
                    removeAt(absorbed)
                    collided = true
                    merged = true
                    break
                }
                j++
            }
            if (!merged) i++
        }
    }

    private fun mergeInto(primary: Int, absorbed: Int) {
        val m1 = mass[primary]
        val m2 = mass[absorbed]
        val totalMass = m1 + m2

        if (totalMass > 0.0) {
            posX[primary] = (posX[primary] * m1 + posX[absorbed] * m2) / totalMass
            posY[primary] = (posY[primary] * m1 + posY[absorbed] * m2) / totalMass
            velX[primary] = (velX[primary] * m1 + velX[absorbed] * m2) / totalMass
            velY[primary] = (velY[primary] * m1 + velY[absorbed] * m2) / totalMass
        }

        mass[primary] = totalMass

        val r1 = radius[primary]
        val r2 = radius[absorbed]
        radius[primary] = cbrt(r1 * r1 * r1 + r2 * r2 * r2)
    }

    private fun removeAt(index: Int) {
        val last = count - 1
        if (index != last) {
            posX[index] = posX[last]
            posY[index] = posY[last]
            velX[index] = velX[last]
            velY[index] = velY[last]
            mass[index] = mass[last]
            radius[index] = radius[last]
            bodies[index] = bodies[last]
        }
        bodies.removeAt(last)
        count = last
    }

    fun positionX(index: Int): Double = posX[index]

    fun positionY(index: Int): Double = posY[index]

    fun syncToObjects() {
        for (i in 0 until count) {
            val body = bodies[i]
            body.position = Vec2(posX[i], posY[i])
            body.velocity = Vec2(velX[i], velY[i])
        }

        if (collided) {
            for (i in 0 until count) {
                bodies[i].mass = Quantity(mass[i], Kilogram)
                bodies[i].radius = Quantity(radius[i], Kilometer)
            }
            collided = false
        }
    }

    fun close() {
        pool?.shutdownNow()
        pool = null
    }
}
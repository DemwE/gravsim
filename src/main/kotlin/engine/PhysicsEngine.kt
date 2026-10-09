package engine

import spaceObjects.GravityObject

/**
 * Single-algorithm (Barnes–Hut) N-body physics engine.
 *
 * Coordinates Structure-of-Arrays (SOA) memory layout, Barnes–Hut quadtree acceleration,
 * multi-threaded force calculation, numerical integration, boundary escaping, and collision resolution.
 */
class PhysicsEngine(
    val bodies: MutableList<GravityObject>,
    initialCapacity: Int = bodies.size,
    private val theta: Double = BARNES_HUT_THETA,
    threads: Int = Runtime.getRuntime().availableProcessors(),
) {
    private val buffers = BodyBuffers(bodies, initialCapacity)
    private val barnesHut = BarnesHut()
    private val dispatcher = ParallelDispatcher(threads)

    val count: Int get() = buffers.count

    fun rebuild() = buffers.rebuild()

    fun step(dt: Double) {
        if (buffers.count == 0 || dt <= 0.0) return

        barnesHut.build(buffers.posX, buffers.posY, buffers.mass, buffers.radius, buffers.count)

        if (dispatcher.threadCount <= 1 || buffers.count < BARNES_HUT_PARALLEL_MIN_BODIES) {
            barnesHut.computeRange(
                buffers.posX,
                buffers.posY,
                buffers.mass,
                buffers.radius,
                buffers.accX,
                buffers.accY,
                0,
                buffers.count,
                theta
            )
        } else {
            dispatcher.executeParallel(buffers.count) { start, end ->
                barnesHut.computeRange(
                    buffers.posX,
                    buffers.posY,
                    buffers.mass,
                    buffers.radius,
                    buffers.accX,
                    buffers.accY,
                    start,
                    end,
                    theta
                )
            }
        }

        Integrator.integrateSymplecticEuler(buffers, dt)
        BoundaryManager.removeEscaped(buffers)
    }

    fun resolveCollisions() = CollisionResolver.resolveCollisions(buffers)

    fun positionX(index: Int): Double = buffers.posX[index]

    fun positionY(index: Int): Double = buffers.posY[index]

    fun syncToObjects() = buffers.syncToObjects()

    fun close() = dispatcher.close()
}

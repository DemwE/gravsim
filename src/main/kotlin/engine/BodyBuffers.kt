package engine

import spaceObjects.*
import units.*

internal class BodyBuffers(
    val bodies: MutableList<GravityObject>,
    initialCapacity: Int = bodies.size,
) {
    var capacity: Int = initialCapacity.coerceAtLeast(0)
        private set

    var count: Int = 0
        private set

    var posX = DoubleArray(capacity)
    var posY = DoubleArray(capacity)
    var velX = DoubleArray(capacity)
    var velY = DoubleArray(capacity)
    var mass = DoubleArray(capacity)
    var radius = DoubleArray(capacity)
    var accX = DoubleArray(capacity)
    var accY = DoubleArray(capacity)

    var hasCollided: Boolean = false
    var lastRecenterX: Double = 0.0
        private set
    var lastRecenterY: Double = 0.0
        private set

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

    fun ensureCapacity(required: Int) {
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

    fun removeAt(index: Int) {
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

    fun syncToObjects() {
        for (i in 0 until count) {
            val body = bodies[i]
            body.position = Vec2(posX[i], posY[i])
            body.velocity = Vec2(velX[i], velY[i])
        }

        if (hasCollided) {
            for (i in 0 until count) {
                bodies[i].mass = Quantity(mass[i], Kilogram)
                bodies[i].radius = Quantity(radius[i], Kilometer)
            }
            hasCollided = false
        }
    }

    fun recenterOnBarycenter() {
        var totalMass = 0.0
        var weightedX = 0.0
        var weightedY = 0.0
        for (i in 0 until count) {
            totalMass += mass[i]
            weightedX += posX[i] * mass[i]
            weightedY += posY[i] * mass[i]
        }
        lastRecenterX = 0.0
        lastRecenterY = 0.0
        if (totalMass <= 0.0) return
        val centerX = weightedX / totalMass
        val centerY = weightedY / totalMass
        if (!centerX.isFinite() || !centerY.isFinite()) return
        lastRecenterX = centerX
        lastRecenterY = centerY
        for (i in 0 until count) {
            posX[i] -= centerX
            posY[i] -= centerY
        }
    }

    fun shiftTrails(offsetX: Double, offsetY: Double) {
        for (body in bodies) {
            for (point in body.trailPoints) {
                point.x((point.x() + offsetX).toFloat())
                point.y((point.y() + offsetY).toFloat())
            }
        }
    }
}

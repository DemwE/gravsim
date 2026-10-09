package engine

/**
 * First-order symplectic (semi-implicit) Euler integrator.
 *
 * Updates velocity first from acceleration, then position from the new velocity.
 * Preserves phase space volume, making it stable for bound orbital systems.
 */
internal object Integrator {

    fun integrateSymplecticEuler(buffers: BodyBuffers, dt: Double) {
        val px = buffers.posX
        val py = buffers.posY
        val vx = buffers.velX
        val vy = buffers.velY
        val ax = buffers.accX
        val ay = buffers.accY
        val count = buffers.count

        for (i in 0 until count) {
            val newVx = vx[i] + ax[i] * dt
            val newVy = vy[i] + ay[i] * dt
            vx[i] = newVx
            vy[i] = newVy
            px[i] += newVx * dt
            py[i] += newVy * dt
        }
    }
}

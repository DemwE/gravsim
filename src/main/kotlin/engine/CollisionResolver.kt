package engine

import kotlin.math.cbrt

internal object CollisionResolver {

    fun resolveCollisions(buffers: BodyBuffers) {
        val px = buffers.posX
        val py = buffers.posY
        val r = buffers.radius
        val m = buffers.mass
        var i = 0

        while (i < buffers.count) {
            var j = i + 1
            var merged = false
            while (j < buffers.count) {
                val dx = px[j] - px[i]
                val dy = py[j] - py[i]
                val contact = r[i] + r[j]

                if (dx * dx + dy * dy <= contact * contact) {
                    val primary = if (m[i] >= m[j]) i else j
                    val absorbed = if (primary == i) j else i
                    mergeInto(buffers, primary, absorbed)
                    buffers.removeAt(absorbed)
                    buffers.hasCollided = true
                    merged = true
                    break
                }
                j++
            }
            if (!merged) i++
        }
    }

    private fun mergeInto(buffers: BodyBuffers, primary: Int, absorbed: Int) {
        val m1 = buffers.mass[primary]
        val m2 = buffers.mass[absorbed]
        val totalMass = m1 + m2

        if (totalMass > 0.0) {
            buffers.posX[primary] = (buffers.posX[primary] * m1 + buffers.posX[absorbed] * m2) / totalMass
            buffers.posY[primary] = (buffers.posY[primary] * m1 + buffers.posY[absorbed] * m2) / totalMass
            buffers.velX[primary] = (buffers.velX[primary] * m1 + buffers.velX[absorbed] * m2) / totalMass
            buffers.velY[primary] = (buffers.velY[primary] * m1 + buffers.velY[absorbed] * m2) / totalMass
        }

        buffers.mass[primary] = totalMass

        val r1 = buffers.radius[primary]
        val r2 = buffers.radius[absorbed]
        buffers.radius[primary] = cbrt(r1 * r1 * r1 + r2 * r2 * r2)
    }
}

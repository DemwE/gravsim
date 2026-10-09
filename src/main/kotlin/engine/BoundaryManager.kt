package engine

import kotlin.math.abs

/**
 * Bounds checking for celestial bodies within the simulation map.
 *
 * Removes bodies that leave the boundary box `[-halfExtentKm, halfExtentKm]^2`.
 */
internal object BoundaryManager {

    fun removeEscaped(buffers: BodyBuffers, halfExtentKm: Double = MAP_HALF_KM) {
        val px = buffers.posX
        val py = buffers.posY
        var i = 0
        while (i < buffers.count) {
            if (abs(px[i]) > halfExtentKm || abs(py[i]) > halfExtentKm) {
                buffers.removeAt(i)
            } else {
                i++
            }
        }
    }
}

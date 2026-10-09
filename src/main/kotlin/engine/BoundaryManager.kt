package engine

import kotlin.math.abs

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

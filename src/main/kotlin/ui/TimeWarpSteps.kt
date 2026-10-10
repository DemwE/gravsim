package ui

object TimeWarpSteps {
    // Fixed simulation seconds per real second, from one day up to 500 days.
    private val scales = doubleArrayOf(
        86_400.0, 864_000.0, 2_160_000.0, 4_320_000.0,
        8_640_000.0, 21_600_000.0, 43_200_000.0,
    )
    private val labels = arrayOf(
        "1d", "10d", "25d", "50d", "100d", "250d", "500d",
    )
    val count: Int get() = scales.size

    fun scaleAt(index: Int): Double = scales[index.coerceIn(0, count - 1)]

    fun labelAt(index: Int): String = labels[index.coerceIn(0, count - 1)]

    fun indexOf(scale: Double): Int = scales.indices.minBy { kotlin.math.abs(scales[it] - scale) }

    fun step(scale: Double, direction: Int): Double {
        val nextIndex = (indexOf(scale) + direction).coerceIn(0, count - 1)
        return scaleAt(nextIndex)
    }
}

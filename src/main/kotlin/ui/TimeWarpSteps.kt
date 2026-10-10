package ui

import units.Day

object TimeWarpSteps {
    // Fixed simulation seconds per real second, from one day up to 500 days.
    private val scales = doubleArrayOf(
        Day.factorToSI, 10.0 * Day.factorToSI, 25.0 * Day.factorToSI,
        50.0 * Day.factorToSI, 100.0 * Day.factorToSI, 250.0 * Day.factorToSI,
        500.0 * Day.factorToSI,
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

package ui

import units.Day

object TimeWarpSteps {
    private val scales = doubleArrayOf(
        Day.factorToSI, 10.0 * Day.factorToSI, 25.0 * Day.factorToSI,
        50.0 * Day.factorToSI, 100.0 * Day.factorToSI, 250.0 * Day.factorToSI,
        365.0 * Day.factorToSI,
    )
    val count: Int get() = scales.size

    fun scaleAt(index: Int): Double = scales[index.coerceIn(0, count - 1)]

    fun indexOf(scale: Double): Int = scales.indices.minBy { kotlin.math.abs(scales[it] - scale) }

    fun step(scale: Double, direction: Int): Double {
        val nextIndex = (indexOf(scale) + direction).coerceIn(0, count - 1)
        return scaleAt(nextIndex)
    }
}

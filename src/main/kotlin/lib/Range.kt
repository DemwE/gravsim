package lib

import units.Quantity
import units.Unit

data class Range<U : Unit<U>>(
    val min: Quantity<U>,
    val max: Quantity<U>,
) {
    init {
        require(min.inSI <= max.inSI) {
            "Min value (${min.inSI}) cannot be greater than max (${max.inSI}) value"
        }
    }

    operator fun contains(value: Double): Boolean = value in min.inSI..max.inSI

    operator fun contains(value: Quantity<U>): Boolean = value.inSI in min.inSI..max.inSI

    fun format(precision: Int = 5): String {
        val formattedMin = min.format(precision)
        val formattedMax = max.format(precision)
        return "$formattedMin - $formattedMax"
    }
}
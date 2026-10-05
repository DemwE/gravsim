package lib

import units.Unit

data class Range<U : Unit<U>>(
    val min: U,
    val max: U,
) {
    init {
        require(min.value <= max.value) { "Min value (${min.value}) cannot be greater than max (${max.value}) value" }
    }

    operator fun contains(value: Double): Boolean = value in min.value..max.value

    fun format(precision: Int = 5): String {
        return "${min.format(precision, min.value)} - ${max.format(precision, max.value)}"
    }
}
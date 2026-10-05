package units

data class Quantity<U : Unit<U>>(
    val value: Double,
    val unit: U
) {
    val inSI: Double get() = unit.toSI(value)

    fun unitSymbol(): String {
        return unit.symbol
    }

    fun unitName(): String {
        return unit.name
    }

    fun format(precision: Int = 4): String {
        return formatWithUnit(value, unit.symbol, precision)
    }
}

internal fun <SOURCE : Unit<SOURCE>, TARGET : Unit<TARGET>> Quantity<SOURCE>.convertToUnit(
    targetUnit: TARGET
): Quantity<TARGET> {
    return Quantity(targetUnit.fromSI(inSI), targetUnit)
}
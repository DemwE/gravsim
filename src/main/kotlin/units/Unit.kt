package units

interface Unit<T : Unit<T>> {
    val name: String
    val symbol: String
    val factorToSI: Double

    fun toSI(value: Double): Double = value * factorToSI
    fun fromSI(value: Double): Double = value / factorToSI
}

fun <T : Unit<T>> unit(value: Number, unit: T): Quantity<T> {
    return Quantity(value.toDouble(), unit)
}

internal fun formatWithUnit(value: Double, symbol: String, precision: Int): String {
    return "%.${precision}f %s".format(value, symbol)
}
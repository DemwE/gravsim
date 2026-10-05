package units

interface Unit<T: Unit<T>> {
    val name: String
    val symbol: String
    var value: Double

    fun toBaseValue(value: Number): Double = value.toDouble()

    fun format(precision: Int = 5, value: Double = this.value): String {
        return "%.${precision}f %s".format(value, symbol)
    }
}
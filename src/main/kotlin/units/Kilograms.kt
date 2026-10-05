package units

data class Kilograms(
    override var value: Double = 1.0
): Unit<Kilograms> {
    override val name: String = "Kilometers"
    override val symbol: String = "km"
}
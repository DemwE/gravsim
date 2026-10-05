package units

data class AstronomicalUnit(
    override var value: Double = 1.0
): Unit<AstronomicalUnit> {
    override val name: String = "Astronomical Unit"
    override val symbol: String = "AU"

    fun toKiloMeters(): Kilometers {
        val kmValue = value * CommonValues.ASTRONOMICAL_UNIT_IN_KILOMETERS.value
        return Kilometers(kmValue)
    }
}
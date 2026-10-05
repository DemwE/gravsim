package units

data class Kilometers(
    override var value: Double = 1.0
): Unit<Kilometers> {
    override val name: String = "Kilometers"
    override val symbol: String = "km"

    fun toMeters(): Int {
        return (value * 1000).toInt()
    }

    fun toAstronomicalUnits(): AstronomicalUnit {
        val astronomicalUnitValue = value / CommonValues.ASTRONOMICAL_UNIT_IN_KILOMETERS.value
        return AstronomicalUnit(astronomicalUnitValue)
    }
}
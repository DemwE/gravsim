package units

data class SunMass(
    override var value: Double = 1.0
): Unit<SunMass> {
    override val name: String = "Sun Mass"
    override val symbol: String = "SM"

    fun toKilograms(): Kilograms {
        val kgValue = value * CommonValues.SUN_MASS_IN_KILOGRAMS.value
        return Kilograms(kgValue)
    }

    fun toEarthMass(): EarthMass {
        val earthMassValue = value * CommonValues.SUN_MASS_IN_KILOGRAMS.value / CommonValues.EARTH_MASS_IN_KILOGRAMS.value
        return EarthMass(earthMassValue)
    }
}
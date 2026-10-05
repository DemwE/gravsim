package units

data class EarthMass(
    override var value: Double = 1.0
): Unit<EarthMass> {
    override val name: String = "Earth Mass"
    override val symbol: String = "EM"

    fun toKilograms(): Kilograms {
        val kgValue = value * CommonValues.EARTH_MASS_IN_KILOGRAMS.value
        return Kilograms(kgValue)
    }

    fun toSunMass(): SunMass {
        val sunMassValue = value * CommonValues.EARTH_MASS_IN_KILOGRAMS.value / CommonValues.SUN_MASS_IN_KILOGRAMS.value
        return SunMass(sunMassValue)
    }
}
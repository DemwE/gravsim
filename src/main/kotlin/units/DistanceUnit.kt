    package units

    interface DistanceUnit<T : DistanceUnit<T>> : Unit<T>

    fun <SOURCE : DistanceUnit<SOURCE>, TARGET : DistanceUnit<TARGET>> Quantity<SOURCE>.convertTo(
        targetUnit: TARGET
    ): Quantity<TARGET> = convertToUnit(targetUnit)

    object Meter : DistanceUnit<Meter> {
        override val name: String = "Meter"
        override val symbol: String = "m"
        override val factorToSI: Double = 1.0
    }

    object Kilometer : DistanceUnit<Kilometer> {
        override val name: String = "Kilometer"
        override val symbol: String = "km"
        override val factorToSI: Double = 1000.0
    }

    object EarthRadius : DistanceUnit<EarthRadius> {
        override val name: String = "Earth Radius"
        override val symbol: String = "R_earth"
        override val factorToSI: Double = 6.371e6
    }

    object SolarRadius : DistanceUnit<SolarRadius> {
        override val name: String = "Solar Radius"
        override val symbol: String = "R_sun"
        override val factorToSI: Double = 6.96340e8
    }

    object AstronomicalUnit : DistanceUnit<AstronomicalUnit> {
        override val name: String = "Astronomical Unit"
        override val symbol: String = "AU"
        override val factorToSI: Double = 1.495978707e11
    }

    object LightYear : DistanceUnit<LightYear> {
        override val name: String = "Light Year"
        override val symbol: String = "ly"
        override val factorToSI: Double = 9.461e15
    }
package units

interface MassUnit<T : MassUnit<T>> : Unit<T>

fun <SOURCE : MassUnit<SOURCE>, TARGET : MassUnit<TARGET>> Quantity<SOURCE>.convertTo(
    targetUnit: TARGET
): Quantity<TARGET> = convertToUnit(targetUnit)

object Kilogram : MassUnit<Kilogram> {
    override val name: String = "Kilogram"
    override val symbol: String = "kg"
    override val factorToSI: Double = 1.0
}

object EarthMass : MassUnit<EarthMass> {
    override val name: String = "Earth Mass"
    override val symbol: String = "M_Earth"
    override val factorToSI: Double = 5.972e24
}

object JupiterMass : MassUnit<JupiterMass> {
    override val name: String = "Jupiter Mass"
    override val symbol: String = "M_Jupiter"
    override val factorToSI: Double = 1.898e27
}

object SolarMass : MassUnit<SolarMass> {
    override val name: String = "Solar Mass"
    override val symbol: String = "M_Sun"
    override val factorToSI: Double = 1.989e30
}
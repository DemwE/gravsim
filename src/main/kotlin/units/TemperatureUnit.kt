package units

interface TemperatureUnit<T : TemperatureUnit<T>> : Unit<T> {
    fun toKelvin(value: Double): Double
    fun fromKelvin(kelvin: Double): Double

    override val factorToSI: Double get() = 1.0
    override fun toSI(value: Double): Double = toKelvin(value)
    override fun fromSI(value: Double): Double = fromKelvin(value)
}

fun <SOURCE : TemperatureUnit<SOURCE>, TARGET : TemperatureUnit<TARGET>> Quantity<SOURCE>.convertTo(
    targetUnit: TARGET
): Quantity<TARGET> = convertToUnit(targetUnit)

object Kelvin : TemperatureUnit<Kelvin> {
    override val name = "Kelvin"
    override val symbol = "K"
    override fun toKelvin(value: Double) = value
    override fun fromKelvin(kelvin: Double) = kelvin
}

object Celsius : TemperatureUnit<Celsius> {
    override val name = "Celsius"
    override val symbol = "C"
    override fun toKelvin(value: Double) = value + 273.15
    override fun fromKelvin(kelvin: Double) = kelvin - 273.15
}

object Fahrenheit : TemperatureUnit<Fahrenheit> {
    override val name = "Fahrenheit"
    override val symbol = "F"
    override fun toKelvin(value: Double) = (value - 32.0) * 5.0 / 9.0 + 273.15
    override fun fromKelvin(kelvin: Double) = (kelvin - 273.15) * 9.0 / 5.0 + 32.0
}
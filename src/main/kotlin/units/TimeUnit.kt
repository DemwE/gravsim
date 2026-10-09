package units

interface TimeUnit<T : TimeUnit<T>> : Unit<T>

fun <SOURCE : TimeUnit<SOURCE>, TARGET : TimeUnit<TARGET>> Quantity<SOURCE>.convertTo(
    targetUnit: TARGET
): Quantity<TARGET> = convertToUnit(targetUnit)

object Second : TimeUnit<Second> {
    override val name: String = "Second"
    override val symbol: String = "s"
    override val factorToSI: Double = 1.0
}

object Minute : TimeUnit<Minute> {
    override val name: String = "Minute"
    override val symbol: String = "min"
    override val factorToSI: Double = 60.0
}

object Hour : TimeUnit<Hour> {
    override val name: String = "Hour"
    override val symbol: String = "h"
    override val factorToSI: Double = 3600.0
}

object Day : TimeUnit<Day> {
    override val name: String = "Day"
    override val symbol: String = "d"
    override val factorToSI: Double = 86400.0
}

object Year : TimeUnit<Year> {
    override val name: String = "Year"
    override val symbol: String = "y"
    override val factorToSI: Double = 365.25 * 86400.0
}

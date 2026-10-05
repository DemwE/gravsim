package bodyTypes

import lib.Range
import units.Kelvin
import units.Kilometer
import units.SolarMass

enum class StarType(
    val DisplayName: String,
    val MassRange: Range<SolarMass>,
    val RadiusRange: Range<Kilometer>,
    val TemperatureRange: Range<Kelvin>,
    val Weight: Float,
) {
}
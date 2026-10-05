package bodyTypes

import lib.Range
import units.EarthMass
import units.Kilometer
import units.unit

enum class CelestialBodyType(
    val Name: String,
    val MassRange: Range<EarthMass>,
    val RadiusRange: Range<Kilometer>,
    val Atmosphere: List<AtmosphereType>,
    val Rarity: Float,
) {
    ICE_BODY (
        Name = "Ice Body",
        MassRange = Range(unit(0.0001, EarthMass), unit(0.1, EarthMass)),
        RadiusRange = Range(unit(100.0, Kilometer), unit(5000.0, Kilometer)),
        Atmosphere = listOf(AtmosphereType.NONE, AtmosphereType.OXYGEN, AtmosphereType.NITROGEN, AtmosphereType.METHANE, AtmosphereType.SULFUR_DIOXIDE, AtmosphereType.NEON_RICH),
        Rarity = 0.2f
    ),
}
package bodyTypes

import units.EarthMass
import units.Kilometers
import lib.Range

enum class CelestialBodyType(
    val Name: String,
    val MassRange: Range<EarthMass>,
    val RadiusRange: Range<Kilometers>,
    val Atmosphere: List<AtmosphereType>,
    val Rarity: Float,
) {
    ICE_BODY (
        Name = "Ice Body",
        MassRange = Range(EarthMass(0.0001), EarthMass(0.1)),
        RadiusRange = Range(Kilometers(100.0), Kilometers(5000.0)),
        Atmosphere = listOf(AtmosphereType.NONE, AtmosphereType.OXYGEN, AtmosphereType.NITROGEN, AtmosphereType.METHANE, AtmosphereType.SULFUR_DIOXIDE, AtmosphereType.NEON_RICH),
        Rarity = 0.2f
    ),
}
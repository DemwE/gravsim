package bodyTypes

import com.raylib.Raylib.Color
import lib.Range
import units.*

enum class CelestialBodyType(
    val displayName: String,
    val massRange: Range<EarthMass>,
    val radiusRange: Range<Kilometer>,
    val atmosphere: List<AtmosphereType>,
    val weight: Float,
    val color: Color,
) {
    // Planet types are based on: https://elite-dangerous.fandom.com/wiki/Planets
    // TERRESTRIAL PLANETS

    // Very Common (~30% all planet spawns)
    ICY_BODY(
        displayName = "Icy Body",
        massRange = Range(unit(0.0001, EarthMass), unit(0.1, EarthMass)),
        radiusRange = Range(unit(100.0, Kilometer), unit(5000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.NONE, AtmosphereType.OXYGEN, AtmosphereType.NITROGEN,
            AtmosphereType.METHANE, AtmosphereType.SULFUR_DIOXIDE, AtmosphereType.NEON_RICH,
            AtmosphereType.ARGON, AtmosphereType.ARGON_RICH
        ),
        weight = 45.0f,
        color = color(180, 205, 230),
    ),

    // Very Common
    ROCKY_BODY(
        displayName = "Rocky Body",
        massRange = Range(unit(0.0001, EarthMass), unit(2.0, EarthMass)),
        radiusRange = Range(unit(300.0, Kilometer), unit(6000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.NONE, AtmosphereType.CARBON_DIOXIDE, AtmosphereType.CARBON_DIOXIDE_RICH,
            AtmosphereType.SULFUR_DIOXIDE, AtmosphereType.METHANE, AtmosphereType.AMMONIA,
            AtmosphereType.ARGON, AtmosphereType.NITROGEN, AtmosphereType.WATER, AtmosphereType.OXYGEN
        ),
        weight = 40.0f,
        color = color(135, 125, 115),
    ),

    // Common
    ROCKY_ICE_WORLD(
        displayName = "Rocky Ice World",
        massRange = Range(unit(0.001, EarthMass), unit(0.5, EarthMass)),
        radiusRange = Range(unit(400.0, Kilometer), unit(5500.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.NONE, AtmosphereType.METHANE, AtmosphereType.NEON_RICH,
            AtmosphereType.ARGON_RICH, AtmosphereType.NITROGEN, AtmosphereType.OXYGEN
        ),
        weight = 20.0f,
        color = color(145, 175, 190),
    ),

    // Common
    HIGH_METAL_CONTENT_WORLD(
        displayName = "High Metal Content World",
        massRange = Range(unit(0.01, EarthMass), unit(10.0, EarthMass)),
        radiusRange = Range(unit(1000.0, Kilometer), unit(12000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.NONE, AtmosphereType.CARBON_DIOXIDE, AtmosphereType.CARBON_DIOXIDE_RICH,
            AtmosphereType.SULFUR_DIOXIDE, AtmosphereType.AMMONIA, AtmosphereType.NITROGEN,
            AtmosphereType.OXYGEN, AtmosphereType.METHANE, AtmosphereType.ARGON
        ),
        weight = 25.0f,
        color = color(130, 145, 155),
    ),

    // Uncommon
    METAL_RICH_BODY(
        displayName = "Metal-Rich Body",
        massRange = Range(unit(0.001, EarthMass), unit(5.0, EarthMass)),
        radiusRange = Range(unit(500.0, Kilometer), unit(8000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.NONE, AtmosphereType.SILICATE_VAPOUR, AtmosphereType.CARBON_DIOXIDE,
            AtmosphereType.SULFUR_DIOXIDE
        ),
        weight = 5.0f,
        color = color(165, 135, 100),
    ),

    // Uncommon
    WATER_WORLD(
        displayName = "Water World",
        massRange = Range(unit(0.1, EarthMass), unit(10.0, EarthMass)),
        radiusRange = Range(unit(3000.0, Kilometer), unit(15000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.WATER, AtmosphereType.CARBON_DIOXIDE, AtmosphereType.NITROGEN,
            AtmosphereType.ARGON_RICH
        ),
        weight = 4.0f,
        color = color(55, 115, 190),
    ),

    // Rare
    AMMONIA_WORLD(
        displayName = "Ammonia World",
        massRange = Range(unit(0.1, EarthMass), unit(8.0, EarthMass)),
        radiusRange = Range(unit(3000.0, Kilometer), unit(13000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.AMMONIA, AtmosphereType.CARBON_DIOXIDE
        ),
        weight = 0.8f,
        color = color(150, 180, 110),
    ),

    // Rare
    EARTH_LIKE_WORLD(
        displayName = "Earth-like World",
        massRange = Range(unit(0.2, EarthMass), unit(2.5, EarthMass)),
        radiusRange = Range(unit(4000.0, Kilometer), unit(9000.0, Kilometer)),
        atmosphere = listOf(
            AtmosphereType.OXYGEN, AtmosphereType.NITROGEN
        ),
        weight = 0.5f,
        color = color(80, 150, 90),
    ),

    // GAS GIANTS

    // Common
    CLASS_I_GAS_GIANT(
        displayName = "Class I Gas Giant",
        massRange = Range(unit(10.0, EarthMass), unit(1000.0, EarthMass)),
        radiusRange = Range(unit(25000.0, Kilometer), unit(80000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.HELIUM, AtmosphereType.AMMONIA, AtmosphereType.METHANE),
        weight = 15.0f,
        color = color(185, 125, 75),
    ),

    // Uncommon
    CLASS_II_GAS_GIANT(
        displayName = "Class II Gas Giant",
        massRange = Range(unit(20.0, EarthMass), unit(1200.0, EarthMass)),
        radiusRange = Range(unit(30000.0, Kilometer), unit(85000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.WATER, AtmosphereType.HELIUM),
        weight = 7.0f,
        color = color(205, 150, 95),
    ),

    // Common
    CLASS_III_GAS_GIANT(
        displayName = "Class III Gas Giant",
        massRange = Range(unit(30.0, EarthMass), unit(1500.0, EarthMass)),
        radiusRange = Range(unit(35000.0, Kilometer), unit(90000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.HELIUM),
        weight = 12.0f,
        color = color(210, 175, 115),
    ),

    // Rare (in standard systems)
    CLASS_IV_GAS_GIANT(
        displayName = "Class IV Gas Giant",
        massRange = Range(unit(50.0, EarthMass), unit(2000.0, EarthMass)),
        radiusRange = Range(unit(40000.0, Kilometer), unit(95000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.HELIUM, AtmosphereType.SILICATE_VAPOUR),
        weight = 1.0f,
        color = color(190, 115, 95),
    ),

    // Rare (in standard systems)
    CLASS_V_GAS_GIANT(
        displayName = "Class V Gas Giant",
        massRange = Range(unit(50.0, EarthMass), unit(2500.0, EarthMass)),
        radiusRange = Range(unit(45000.0, Kilometer), unit(100000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.SILICATE_VAPOUR),
        weight = 0.6f,
        color = color(115, 145, 200),
    ),

    // Uncommon
    GAS_GIANT_WITH_AMMONIA_LIFE(
        displayName = "Gas Giant with Ammonia-based Life",
        massRange = Range(unit(10.0, EarthMass), unit(800.0, EarthMass)),
        radiusRange = Range(unit(25000.0, Kilometer), unit(75000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.AMMONIA, AtmosphereType.OXYGEN),
        weight = 3.5f,
        color = color(145, 175, 95),
    ),

    // Uncommon
    GAS_GIANT_WITH_WATER_LIFE(
        displayName = "Gas Giant with Water-based Life",
        massRange = Range(unit(10.0, EarthMass), unit(800.0, EarthMass)),
        radiusRange = Range(unit(25000.0, Kilometer), unit(75000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.WATER, AtmosphereType.OXYGEN),
        weight = 3.5f,
        color = color(70, 145, 185),
    ),

    // Rare
    WATER_GIANT(
        displayName = "Water Giant",
        massRange = Range(unit(5.0, EarthMass), unit(200.0, EarthMass)),
        radiusRange = Range(unit(15000.0, Kilometer), unit(50000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.WATER, AtmosphereType.CARBON_DIOXIDE_RICH),
        weight = 1.0f,
        color = color(55, 125, 220),
    ),

    // Very Rare
    HELIUM_RICH_GAS_GIANT(
        displayName = "Helium-rich Gas Giant",
        massRange = Range(unit(20.0, EarthMass), unit(1000.0, EarthMass)),
        radiusRange = Range(unit(30000.0, Kilometer), unit(80000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.HELIUM),
        weight = 0.2f,
        color = color(220, 195, 135),
    ),

    // Extremely Rare
    HELIUM_GAS_GIANT(
        displayName = "Helium Gas Giant",
        massRange = Range(unit(10.0, EarthMass), unit(500.0, EarthMass)),
        radiusRange = Range(unit(20000.0, Kilometer), unit(60000.0, Kilometer)),
        atmosphere = listOf(AtmosphereType.HELIUM),
        weight = 0.02f,
        color = color(235, 215, 165),
    ),
}
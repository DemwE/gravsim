package bodyTypes

import com.raylib.Raylib.Color

enum class AtmosphereType(
    val displayName: String,
    val weight: Float,
    val color: Color? = null,
) {
    // Atmosphere types are based on: https://elite-dangerous.fandom.com/wiki/Atmosphere

    // SUPER RARE (~0.05% - 0.2% chance)
    SILICATE_VAPOUR("Silicate Vapour", 0.05f, color(235, 115, 75)),

    // VERY RARE (~0.5% - 1% chance)
    OXYGEN("Oxygen", 0.2f, color(105, 175, 235)),
    WATER("Water", 0.2f, color(55, 145, 210)),
    NITROGEN("Nitrogen", 0.4f, color(135, 180, 235)),
    HELIUM("Helium Rich", 0.5f, color(240, 205, 135)),

    // RARE / UNCOMMON (~2% - 5% chance)
    AMMONIA("Ammonia", 1.0f, color(160, 205, 115)),
    NEON_RICH("Neon Rich", 1.2f, color(235, 105, 150)),
    ARGON_RICH("Argon Rich", 1.5f, color(145, 105, 195)),
    ARGON("Argon", 2.0f, color(115, 130, 205)),
    CARBON_DIOXIDE_RICH("Carbon Dioxide Rich", 2.5f, color(195, 105, 75)),

    // COMMON (~10% - 15% chance)
    METHANE("Methane", 8.0f, color(90, 190, 185)),
    SULFUR_DIOXIDE("Sulfur Dioxide", 12.0f, color(205, 190, 75)),
    CARBON_DIOXIDE("Carbon Dioxide", 20.0f, color(175, 115, 90)),

    // EXTREMELY COMMON (~50%+ chance)
    // Most landable planets and moons in ED lack an atmosphere altogether
    NONE("None", 50.0f, null);

    fun colorWithOpacity(opacity: Int = 90): Color {
        val base = color ?: error("AtmosphereType.NONE nie posiada koloru atmosfery")
        require(opacity in 0..255) { "Przezroczystość musi mieścić się w przedziale 0..255" }
        return color(base.r().toInt() and 0xFF, base.g().toInt() and 0xFF, base.b().toInt() and 0xFF, opacity)
    }
}
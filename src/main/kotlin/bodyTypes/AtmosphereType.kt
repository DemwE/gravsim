package bodyTypes

enum class AtmosphereType(
    val displayName: String,
    val weight: Float,
) {
    // Atmosphere types are based on: https://elite-dangerous.fandom.com/wiki/Atmosphere

    // SUPER RARE (~0.05% - 0.2% chance)
    SILICATE_VAPOUR("Silicate Vapour", 0.05f),

    // VERY RARE (~0.5% - 1% chance)
    OXYGEN("Oxygen", 0.2f),
    WATER("Water", 0.2f),
    NITROGEN("Nitrogen", 0.4f),
    HELIUM("Helium Rich", 0.5f),

    // RARE / UNCOMMON (~2% - 5% chance)
    AMMONIA("Ammonia", 1.0f),
    NEON_RICH("Neon Rich", 1.2f),
    ARGON_RICH("Argon Rich", 1.5f),
    ARGON("Argon", 2.0f),
    CARBON_DIOXIDE_RICH("Carbon Dioxide Rich", 2.5f),

    // COMMON (~10% - 15% chance)
    METHANE("Methane", 8.0f),
    SULFUR_DIOXIDE("Sulfur Dioxide", 12.0f),
    CARBON_DIOXIDE("Carbon Dioxide", 20.0f),

    // EXTREMELY COMMON (~50%+ chance)
    // Most landable planets and moons in ED lack an atmosphere altogether
    NONE("None", 50.0f),
}
package bodyTypes

enum class AtmosphereType(
    val DisplayName: String,
    val Weight: Float,
) {
    // SUPER RARE
    SILICATE_VAPOUR("Silicate Vapour", 0.1f),

    // RARE
    OXYGEN("Oxygen", 0.3f),
    NITROGEN("Nitrogen", 0.3f),
    WATER("Water", 0.3f),
    HELIUM("Helium Rich", 0.3f),

    // UNCOMMON
    AMMONIA("Ammonia", 0.7f),
    METHANE("Methane", 0.7f),
    ARGON("Argon", 0.7f),
    NEON_RICH("Neon Rich", 0.7f),
    ARGON_RICH("Argon Rich", 0.7f),
    CARBON_DIOXIDE_RICH("Carbon Dioxide Rich", 0.7f),

    // COMMON
    CARBON_DIOXIDE("Carbon Dioxide", 1.0f),
    SULFUR_DIOXIDE("Sulfur Dioxide", 1.0f),
    NONE("None", 2.0f),
}
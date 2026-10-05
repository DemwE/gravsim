package bodyTypes

import com.raylib.Raylib.Color

object BodyColors {
    fun celestialBody(type: CelestialBodyType): Color = surface(type)

    fun atmosphere(type: AtmosphereType, opacity: Int = 90): Color {
        require(opacity in 0..255) { "Opacity must be between 0 and 255" }
        return when (type) {
        AtmosphereType.SILICATE_VAPOUR -> color(235, 115, 75)
        AtmosphereType.OXYGEN -> color(105, 175, 235)
        AtmosphereType.WATER -> color(55, 145, 210)
        AtmosphereType.NITROGEN -> color(135, 180, 235)
        AtmosphereType.HELIUM -> color(240, 205, 135)
        AtmosphereType.AMMONIA -> color(160, 205, 115)
        AtmosphereType.NEON_RICH -> color(235, 105, 150)
        AtmosphereType.ARGON_RICH -> color(145, 105, 195)
        AtmosphereType.ARGON -> color(115, 130, 205)
        AtmosphereType.CARBON_DIOXIDE_RICH -> color(195, 105, 75)
        AtmosphereType.METHANE -> color(90, 190, 185)
        AtmosphereType.SULFUR_DIOXIDE -> color(205, 190, 75)
        AtmosphereType.CARBON_DIOXIDE -> color(175, 115, 90)
        AtmosphereType.NONE -> error("Atmosphere.NONE has no atmospheric color")
        }.a(opacity.toByte())
    }

    private fun surface(type: CelestialBodyType): Color = when (type) {
        CelestialBodyType.ICY_BODY -> color(180, 205, 230)
        CelestialBodyType.ROCKY_BODY -> color(135, 125, 115)
        CelestialBodyType.ROCKY_ICE_WORLD -> color(145, 175, 190)
        CelestialBodyType.HIGH_METAL_CONTENT_WORLD -> color(130, 145, 155)
        CelestialBodyType.METAL_RICH_BODY -> color(165, 135, 100)
        CelestialBodyType.WATER_WORLD -> color(55, 115, 190)
        CelestialBodyType.AMMONIA_WORLD -> color(150, 180, 110)
        CelestialBodyType.EARTH_LIKE_WORLD -> color(80, 150, 90)
        CelestialBodyType.CLASS_I_GAS_GIANT -> color(185, 125, 75)
        CelestialBodyType.CLASS_II_GAS_GIANT -> color(205, 150, 95)
        CelestialBodyType.CLASS_III_GAS_GIANT -> color(210, 175, 115)
        CelestialBodyType.CLASS_IV_GAS_GIANT -> color(190, 115, 95)
        CelestialBodyType.CLASS_V_GAS_GIANT -> color(115, 145, 200)
        CelestialBodyType.GAS_GIANT_WITH_AMMONIA_LIFE -> color(145, 175, 95)
        CelestialBodyType.GAS_GIANT_WITH_WATER_LIFE -> color(70, 145, 185)
        CelestialBodyType.WATER_GIANT -> color(55, 125, 220)
        CelestialBodyType.HELIUM_RICH_GAS_GIANT -> color(220, 195, 135)
        CelestialBodyType.HELIUM_GAS_GIANT -> color(235, 215, 165)
    }

    private fun color(red: Int, green: Int, blue: Int): Color =
        Color()
            .r(red.toByte())
            .g(green.toByte())
            .b(blue.toByte())
            .a(255.toByte())
}

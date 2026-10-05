package spaceObjects

import bodyTypes.AtmosphereType
import bodyTypes.BodyColors
import bodyTypes.CelestialBodyType
import com.raylib.Raylib.*
import units.EarthMass
import units.Kilometer
import units.Quantity

data class CelestialBody(
    override val name: String,
    val type: CelestialBodyType,
    val atmosphere: AtmosphereType,
    val mass: Quantity<EarthMass>,
    val radius: Quantity<Kilometer>,
    override var position: Vector2 = Vector2().x(0f).y(0f)
) : Object {
    init {
        require(atmosphere in type.atmosphere) {
            "${atmosphere.displayName} is not a valid atmosphere for ${type.displayName}"
        }
    }

    val color: Color
        get() = BodyColors.celestialBody(type)

    override fun draw() {
        if (atmosphere != AtmosphereType.NONE) {
            val atmosphereColor = BodyColors.atmosphere(atmosphere, opacity = 75)
            val transparentAtmosphere = Color()
                .r(atmosphereColor.r())
                .g(atmosphereColor.g())
                .b(atmosphereColor.b())
                .a(0.toByte())
            DrawCircleGradient(
                position,
                radius.value.toFloat() * 1.5f,
                atmosphereColor,
                transparentAtmosphere
            )
        }
        drawSmoothCircle(position, radius.value.toFloat(), color)
    }
}

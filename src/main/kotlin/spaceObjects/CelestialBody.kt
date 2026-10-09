package spaceObjects

import bodyTypes.AtmosphereType
import bodyTypes.BodyColors
import bodyTypes.CelestialBodyType
import com.raylib.Raylib.*
import units.Kilogram
import units.Kilometer
import units.Quantity

data class CelestialBody(
    override val name: String,
    val type: CelestialBodyType,
    val atmosphere: AtmosphereType,
    override var mass: Quantity<Kilogram>,
    override var radius: Quantity<Kilometer>,
    override var velocity: Vec2 = Vec2(),
    override var position: Vec2 = Vec2(),
    override val trailPoints: MutableList<Vector2> = mutableListOf(),
    override var maxTrailPoints: Int = 800,
    override var trailSegmentKm: Double = 1000.0
) : GravityObject {
    init {
        require(atmosphere in type.atmosphere) {
            "${atmosphere.displayName} is not a valid atmosphere for ${type.displayName}"
        }
    }

    val color: Color
        get() = BodyColors.celestialBody(type)

    override fun draw() {
        val screenPosition = position.toVector2()

        // 1. Ślad orbity pod planetą
        val trailBaseColor = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(200.toByte())
        drawTrail(trailBaseColor)

        // 2. Atmosfera
        if (atmosphere != AtmosphereType.NONE) {
            val atmosphereColor = BodyColors.atmosphere(atmosphere, opacity = 75)
            val transparentAtmosphere = Color()
                .r(atmosphereColor.r())
                .g(atmosphereColor.g())
                .b(atmosphereColor.b())
                .a(0.toByte())
            DrawCircleGradient(
                screenPosition,
                radius.value.toFloat() * 1.5f,
                atmosphereColor,
                transparentAtmosphere
            )
        }

        // 3. Sama planeta
        drawSmoothCircle(screenPosition, radius.value.toFloat(), color)
    }
}

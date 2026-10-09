package spaceObjects

import bodyTypes.AtmosphereType
import bodyTypes.CelestialBodyType
import com.raylib.Raylib.*
import units.Kilogram
import units.Kilometer
import units.Quantity

class CelestialBody(
    name: String,
    val type: CelestialBodyType,
    val atmosphere: AtmosphereType,
    mass: Quantity<Kilogram>,
    radius: Quantity<Kilometer>,
    position: Vec2 = Vec2(),
    velocity: Vec2 = Vec2(),
    trailPoints: ArrayDeque<Vector2> = ArrayDeque(),
    maxTrailPoints: Int = 800,
    trailSegmentKm: Double = 1000.0,
) : GravityObject(
    name = name,
    mass = mass,
    radius = radius,
    position = position,
    velocity = velocity,
    trailPoints = trailPoints,
    maxTrailPoints = maxTrailPoints,
    trailSegmentKm = trailSegmentKm,
) {
    init {
        require(atmosphere in type.atmosphere) {
            "${atmosphere.displayName} is not a valid atmosphere for ${type.displayName}"
        }
    }

    val color: Color
        get() = type.color

    private val trailBaseColor: Color by lazy {
        Color().r(color.r()).g(color.g()).b(color.b()).a(200.toByte())
    }

    private val atmosphereColor: Color by lazy {
        atmosphere.colorWithOpacity(opacity = 75)
    }

    private val transparentAtmosphere: Color by lazy {
        Color().r(atmosphereColor.r()).g(atmosphereColor.g()).b(atmosphereColor.b()).a(0.toByte())
    }

    override fun draw() {
        renderPosition.x(position.x.toFloat()).y(position.y.toFloat())

        drawTrail(trailBaseColor)

        if (atmosphere != AtmosphereType.NONE) {
            DrawCircleGradient(
                renderPosition,
                radius.value.toFloat() * 1.5f,
                atmosphereColor,
                transparentAtmosphere,
            )
        }

        drawSmoothCircle(renderPosition, radius.value.toFloat(), color)
    }
}

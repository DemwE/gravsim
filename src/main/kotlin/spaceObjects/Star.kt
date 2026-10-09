package spaceObjects

import bodyTypes.StarType
import com.raylib.Raylib.*
import units.Kelvin
import units.Kilogram
import units.Kilometer
import units.Quantity

class Star(
    name: String,
    val type: StarType,
    mass: Quantity<Kilogram>,
    radius: Quantity<Kilometer>,
    val temperature: Quantity<Kelvin>,
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
    val color: Color
        get() = type.color

    private val trailBaseColor: Color by lazy {
        Color().r(color.r()).g(color.g()).b(color.b()).a(180.toByte())
    }

    private val glowColorInner: Color by lazy {
        Color().r(color.r()).g(color.g()).b(color.b()).a(120.toByte())
    }

    private val glowColorOuter: Color by lazy {
        Color().r(color.r()).g(color.g()).b(color.b()).a(0.toByte())
    }

    override fun draw() {
        renderPosition.x(position.x.toFloat()).y(position.y.toFloat())

        drawTrail(trailBaseColor)

        val glowRadius = (radius.value * 2.5).toFloat()
        DrawCircleGradient(
            renderPosition,
            glowRadius,
            glowColorInner,
            glowColorOuter,
        )

        drawSmoothCircle(renderPosition, radius.value.toFloat(), color)
    }
}

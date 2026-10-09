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
    trailPoints: MutableList<Vector2> = mutableListOf(),
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

    private fun starGlow(color: Color): Pair<Color, Color> {
        val glowColorInner = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(120.toByte())

        val glowColorOuter = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(0.toByte())

        return Pair(glowColorInner, glowColorOuter)
    }

    override fun draw() {
        val screenPosition = position.toVector2()
        val starColor = color
        val (glowColorInner, glowColorOuter) = starGlow(starColor)

        // 1. Ślad trajektorii gwiazdy (pod poświatą)
        val trailBaseColor = Color()
            .r(starColor.r())
            .g(starColor.g())
            .b(starColor.b())
            .a(180.toByte())
        drawTrail(trailBaseColor)

        // 2. Poświata
        val glowRadius = (radius.value * 2.5).toFloat()
        DrawCircleGradient(
            screenPosition,
            glowRadius,
            glowColorInner,
            glowColorOuter,
        )

        // 3. Rdzeń
        drawSmoothCircle(screenPosition, radius.value.toFloat(), starColor)
    }
}

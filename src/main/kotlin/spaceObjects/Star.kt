package spaceObjects

import bodyTypes.StarColors
import bodyTypes.StarType
import com.raylib.Raylib.*
import units.Kelvin
import units.Kilogram
import units.Kilometer
import units.Quantity

data class Star(
    override val name: String,
    val type: StarType,
    override var mass: Quantity<Kilogram>,
    override var radius: Quantity<Kilometer>,
    val temperature: Quantity<Kelvin>,
    override var velocity: Vec2 = Vec2(),
    override var position: Vec2 = Vec2(),
    override val trailPoints: MutableList<Vector2> = mutableListOf(),
    override var maxTrailPoints: Int = 800,
    override var trailSegmentKm: Double = 1000.0
) : GravityObject {
    fun starGlow(color: Color): Pair<Color, Color> {
        val glowColorInner: Color = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(120.toByte())

        val glowColorOuter: Color = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(0.toByte())

        return Pair(glowColorInner, glowColorOuter)
    }

    override fun draw() {
        val screenPosition = position.toVector2()
        val color = StarColors.star(type)
        val (glowColorInner, glowColorOuter) = starGlow(color)

        // 1. Ślad trajektorii gwiazdy (pod poświatą)
        val trailBaseColor = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(180.toByte())
        drawTrail(trailBaseColor)

        // 2. Poświata
        val glowRadius = (radius.value * 2.5).toFloat()
        DrawCircleGradient(
            screenPosition,
            glowRadius,
            glowColorInner,
            glowColorOuter
        )

        // 3. Rdzeń
        drawSmoothCircle(screenPosition, radius.value.toFloat(), color)
    }
}

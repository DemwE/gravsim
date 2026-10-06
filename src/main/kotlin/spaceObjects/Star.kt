package spaceObjects

import bodyTypes.StarType
import bodyTypes.StarColors
import com.raylib.Raylib.*
import units.Kelvin
import units.Kilometer
import units.Quantity
import units.SolarMass

data class Star(
    override val name: String,
    val type: StarType,
    val mass: Quantity<SolarMass>,
    val radius: Quantity<Kilometer>,
    val temperature: Quantity<Kelvin>,
    override var position: Vector2 = Vector2().x(0f).y(0f)
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
        val color = StarColors.star(type)
        val (glowColorInner, glowColorOuter) = starGlow(color)



        // 1. Rysowanie poświaty gwiazdy (Glow effect)
        val glowRadius = (radius.value * 2.5).toFloat()
        DrawCircleGradient(
            position,
            glowRadius,
            glowColorInner,
            glowColorOuter
        )

        // 2. Rysowanie rdzenia gwiazdy
        drawSmoothCircle(position, radius.value.toFloat(), color)
    }
}

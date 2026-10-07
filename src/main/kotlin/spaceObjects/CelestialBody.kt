package spaceObjects

import bodyTypes.AtmosphereType
import bodyTypes.BodyColors
import bodyTypes.CelestialBodyType
import com.raylib.Raylib.*
import units.EarthMass
import units.Kilogram
import units.Kilometer
import units.Quantity

data class CelestialBody(
    override val name: String,
    val type: CelestialBodyType,
    val atmosphere: AtmosphereType,
    override val mass: Quantity<Kilogram>,
    val radius: Quantity<Kilometer>,
    override var velocity: Vector2 = Vector2().x(0f).y(0f),
    override var position: Vector2 = Vector2().x(0f).y(0f),
    override val trailPoints: MutableList<Vector2> = mutableListOf(),
    override var maxTrailPoints: Int = 300
) : GravityObject {
    init {
        require(atmosphere in type.atmosphere) {
            "${atmosphere.displayName} is not a valid atmosphere for ${type.displayName}"
        }
    }

    val color: Color
        get() = BodyColors.celestialBody(type)

    override fun draw() {
        // 1. Rysujemy ślad orbity NAJPIERW (dzięki temu znajduje się pod planetą)
        // Kolor paska dopasowujemy np. do koloru planety z wyższą przezroczystością
        val trailBaseColor = Color()
            .r(color.r())
            .g(color.g())
            .b(color.b())
            .a(200.toByte())
        drawTrail(trailBaseColor)

        // 2. Rysujemy atmosferę
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

        // 3. Rysujemy samą planetę
        drawSmoothCircle(position, radius.value.toFloat(), color)
    }
}
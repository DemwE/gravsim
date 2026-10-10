package spaceObjects

import bodyTypes.AtmosphereType
import bodyTypes.CelestialBodyType
import com.raylib.Raylib.*
import config.TrailConfig
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
    maxTrailPoints: Int = TrailConfig.DEFAULT_MAX_POINTS,
    trailSegmentKm: Double = TrailConfig.DEFAULT_SEGMENT_KM,
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
        Color().r(color.r()).g(color.g()).b(color.b()).a(TrailConfig.PLANET_TRAIL_ALPHA.toByte())
    }

    override fun draw() {
        renderPosition.x(position.x.toFloat()).y(position.y.toFloat())
        drawTrail(trailBaseColor)
    }
}

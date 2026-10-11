package spaceObjects

import bodyTypes.StarType
import com.raylib.Raylib.*
import config.TrailConfig
import units.*

class Star(
    name: String,
    val type: StarType,
    mass: Quantity<Kilogram>,
    radius: Quantity<Kilometer>,
    val temperature: Quantity<Kelvin>,
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
    val color: Color
        get() = type.color

    private val trailBaseColor: Color by lazy {
        Color().r(color.r()).g(color.g()).b(color.b()).a(TrailConfig.STAR_TRAIL_ALPHA.toByte())
    }

    override fun draw() {
        renderPosition.x(position.x.toFloat()).y(position.y.toFloat())
        drawTrail(trailBaseColor)
    }
}

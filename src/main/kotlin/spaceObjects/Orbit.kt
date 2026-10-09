package spaceObjects

import engine.G
import kotlin.math.sqrt

fun setCircularOrbit(
    satellite: GravityObject,
    centers: List<GravityObject>,
    clockwise: Boolean = true
) {
    require(centers.isNotEmpty()) { "Orbit requires at least one central body" }

    val totalMass = centers.sumOf { it.mass.value }
    require(totalMass > 0.0) { "Total central mass must be positive" }

    val cx = centers.sumOf { it.position.x * it.mass.value } / totalMass
    val cy = centers.sumOf { it.position.y * it.mass.value } / totalMass
    val cvx = centers.sumOf { it.velocity.x * it.mass.value } / totalMass
    val cvy = centers.sumOf { it.velocity.y * it.mass.value } / totalMass

    val dx = satellite.position.x - cx
    val dy = satellite.position.y - cy
    val r = sqrt(dx * dx + dy * dy)
    if (r == 0.0) return

    val speed = sqrt(G * totalMass / r)
    val tangentX = if (clockwise) -dy else dy
    val tangentY = if (clockwise) dx else -dx

    satellite.velocity = Vec2(
        cvx + tangentX / r * speed,
        cvy + tangentY / r * speed
    )
}

fun setBinaryStarOrbit(star1: GravityObject, star2: GravityObject) {
    val dx = star2.position.x - star1.position.x
    val dy = star2.position.y - star1.position.y
    val r = sqrt(dx * dx + dy * dy)
    if (r == 0.0) return

    val m1 = star1.mass.value
    val m2 = star2.mass.value
    val totalMass = m1 + m2

    val vRel = sqrt(G * totalMass / r)
    val nx = -dy / r
    val ny = dx / r

    star1.velocity = Vec2(-nx * vRel * (m2 / totalMass), -ny * vRel * (m2 / totalMass))
    star2.velocity = Vec2(nx * vRel * (m1 / totalMass), ny * vRel * (m1 / totalMass))
}

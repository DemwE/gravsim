package spaceObjects

import com.raylib.Raylib.*
import kotlin.math.sqrt

fun setCircumbinaryOrbit(
    satellite: GravityObject,
    star1: GravityObject,
    star2: GravityObject,
    G: Float,
    clockwise: Boolean = true
) {
    val m1 = star1.mass.value.toFloat()
    val m2 = star2.mass.value.toFloat()
    val totalMass = m1 + m2

    // 1. Wyznaczenie środka masy obu gwiazd
    val cx = (star1.position.x() * m1 + star2.position.x() * m2) / totalMass
    val cy = (star1.position.y() * m1 + star2.position.y() * m2) / totalMass

    // Średnia prędkość środka masy
    val cvx = (star1.velocity.x() * m1 + star2.velocity.x() * m2) / totalMass
    val cvy = (star1.velocity.y() * m1 + star2.velocity.y() * m2) / totalMass

    // 2. Wektor od środka masy do satelity
    val dx = satellite.position.x() - cx
    val dy = satellite.position.y() - cy
    val r = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

    if (r == 0f) return

    // 3. Wartość prędkości wzgledem łącznej masy
    val speed = sqrt((G * totalMass / r).toDouble()).toFloat()

    // 4. Wektor prostopadły
    val tangentX = if (clockwise) -dy else dy
    val tangentY = if (clockwise) dx else -dx

    val nx = tangentX / r
    val ny = tangentY / r

    satellite.velocity = Vector2()
        .x(cvx + nx * speed)
        .y(cvy + ny * speed)
}

fun setBinaryStarOrbit(star1: GravityObject, star2: GravityObject, G: Float) {
    val dx = star2.position.x() - star1.position.x()
    val dy = star2.position.y() - star1.position.y()
    val r = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

    val m1 = star1.mass.value.toFloat()
    val m2 = star2.mass.value.toFloat()
    val totalMass = m1 + m2

    // Prędkość całkowita w układzie względnym: v = sqrt(G * (m1 + m2) / r)
    val vRel = sqrt((G * totalMass / r).toDouble()).toFloat()

    // Wektor prostopadły znormalizowany
    val nx = -dy / r
    val ny = dx / r

    // Prędkości dzielą się odwrotnie proporcjonalnie do mas
    star1.velocity = Vector2()
        .x(-nx * vRel * (m2 / totalMass))
        .y(-ny * vRel * (m2 / totalMass))
    star2.velocity = Vector2()
        .x(nx * vRel * (m1 / totalMass))
        .y(ny * vRel * (m1 / totalMass))
}
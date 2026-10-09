import com.raylib.Raylib.Vector2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import spaceObjects.GravityObject
import spaceObjects.Vec2
import spaceObjects.resolveCollisions
import spaceObjects.stepPhysics
import units.Kilogram
import units.Kilometer
import units.Quantity
import units.unit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Testowe ciało — bez rysowania i bez śladu, więc testy nie dotykają natywnego raylib.
 */
private class TestBody(
    override var mass: Quantity<Kilogram>,
    override var radius: Quantity<Kilometer>,
    override var position: Vec2 = Vec2(),
    override var velocity: Vec2 = Vec2(),
) : GravityObject {
    override val name: String = "test"
    override val trailPoints: MutableList<Vector2> = mutableListOf()
    override var maxTrailPoints: Int = 10
    override var trailSegmentKm: Double = 1.0

    override fun draw() {}
    override fun updateTrail() {}
}

class PhysicsTest {

    @Test
    fun circularOrbitStaysBound() {
        val starMass = 1.989e30
        val planetMass = 5.972e24
        val r = 1.496e8

        val star = TestBody(unit(starMass, Kilogram), unit(7.0e5, Kilometer))
        val planet = TestBody(unit(planetMass, Kilogram), unit(6.371e3, Kilometer), Vec2(r, 0.0))

        val mu = 6.67430e-20 * (starMass + planetMass)
        val vRel = sqrt(mu / r)
        planet.velocity = Vec2(0.0, vRel * starMass / (starMass + planetMass))
        star.velocity = Vec2(0.0, -vRel * planetMass / (starMass + planetMass))

        val period = 2.0 * PI * sqrt(r * r * r / mu)
        val steps = 20_000
        val bodies = listOf<GravityObject>(star, planet)

        repeat(steps) { stepPhysics(bodies, period / steps) }

        val separation = (planet.position - star.position).length()
        assertTrue(
            abs(separation - r) / r < 0.02,
            "Po jednym okresie separacja powinna zostać ~$r km, jest $separation km"
        )
    }

    @Test
    fun collisionMergesAndConservesMomentum() {
        val a = TestBody(
            unit(6.0e24, Kilogram), unit(1.0e4, Kilometer),
            Vec2(-1.0e4, 0.0), Vec2(10.0, 0.0)
        )
        val b = TestBody(
            unit(3.0e24, Kilogram), unit(1.0e4, Kilometer),
            Vec2(1.0e4, 0.0), Vec2(-20.0, 0.0)
        )
        val objects = mutableListOf<GravityObject>(a, b)
        val momentumBefore = a.velocity.x * a.mass.value + b.velocity.x * b.mass.value

        resolveCollisions(objects)

        assertEquals(1, objects.size, "Dwa nachodzące ciała powinny się scalić")
        val survivor = objects.first()
        assertEquals(9.0e24, survivor.mass.value, 1e20, "Masa po scaleniu powinna być sumą mas")
        assertEquals(
            momentumBefore,
            survivor.velocity.x * survivor.mass.value,
            1e20,
            "Pęd powinien być zachowany"
        )
    }

    @Test
    fun closeApproachDoesNotSlingshot() {
        val objects = mutableListOf<GravityObject>(
            TestBody(
                unit(6.0e24, Kilogram), unit(1.0e4, Kilometer),
                Vec2(-2.0e4, 0.0), Vec2(5.0, 0.0)
            ),
            TestBody(
                unit(6.0e24, Kilogram), unit(1.0e4, Kilometer),
                Vec2(2.0e4, 0.0), Vec2(-5.0, 0.0)
            )
        )

        var steps = 0
        while (objects.size > 1 && steps < 10_000) {
            stepPhysics(objects, 600.0)
            resolveCollisions(objects)
            steps++
        }

        assertEquals(1, objects.size, "Bliskie podejście powinno zakończyć się scaleniem")
        val speed = objects.first().velocity.length()
        assertTrue(speed < 100.0, "Scalenie nie może wystrzelić ciała: predkosc=$speed km/s")
    }
}

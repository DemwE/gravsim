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

private class TestBody(
    mass: Quantity<Kilogram>,
    radius: Quantity<Kilometer>,
    position: Vec2 = Vec2(),
    velocity: Vec2 = Vec2(),
    name: String = "test",
) : GravityObject(
    name = name,
    mass = mass,
    radius = radius,
    position = position,
    velocity = velocity,
    maxTrailPoints = 10,
    trailSegmentKm = 1.0,
) {
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
            "After one period separation should remain ~$r km, got $separation km"
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

        assertEquals(1, objects.size, "Overlapping bodies should merge")
        val survivor = objects.first()
        assertEquals(9.0e24, survivor.mass.value, 1e20, "Merged mass should be sum of masses")
        assertEquals(
            momentumBefore,
            survivor.velocity.x * survivor.mass.value,
            1e20,
            "Momentum should be conserved"
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

        assertEquals(1, objects.size, "Close approach should result in merge")
        val speed = objects.first().velocity.length()
        assertTrue(speed < 100.0, "Merge should not slingshot body: speed=$speed km/s")
    }

    @Test
    fun updateTrailRecordsInitialPositionAndSegments() {
        val body = object : GravityObject(
            name = "trailTest",
            mass = unit(1.0, Kilogram),
            radius = unit(1.0, Kilometer),
            position = Vec2(0.0, 0.0),
            maxTrailPoints = 3,
            trailSegmentKm = 10.0,
        ) {
            override fun draw() {}
        }

        body.updateTrail()
        assertEquals(1, body.trailPoints.size)
        assertEquals(0.0f, body.trailPoints[0].x())
        assertEquals(0.0f, body.trailPoints[0].y())

        body.position = Vec2(5.0, 0.0)
        body.updateTrail()
        assertEquals(1, body.trailPoints.size)

        body.position = Vec2(12.0, 0.0)
        body.updateTrail()
        assertEquals(2, body.trailPoints.size)

        body.position = Vec2(25.0, 0.0)
        body.updateTrail()
        assertEquals(3, body.trailPoints.size)

        body.position = Vec2(40.0, 0.0)
        body.updateTrail()
        assertEquals(3, body.trailPoints.size)
        assertEquals(12.0f, body.trailPoints[0].x())
    }
}

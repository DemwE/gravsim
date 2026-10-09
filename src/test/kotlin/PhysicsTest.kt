import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import engine.BarnesHut
import engine.G
import spaceObjects.GravityObject
import engine.MAP_HALF_KM
import engine.PhysicsEngine
import spaceObjects.Vec2
import spaceObjects.setBinaryStarOrbit
import spaceObjects.setCircularOrbit
import units.Kilogram
import units.Kilometer
import units.Quantity
import units.unit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

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
        val engine = PhysicsEngine(mutableListOf<GravityObject>(star, planet))
        engine.rebuild()

        repeat(steps) { engine.step(period / steps) }
        engine.syncToObjects()

        val separation = (planet.position - star.position).length()
        assertTrue(
            abs(separation - r) / r < 0.02,
            "After one period separation should remain ~$r km, got $separation km"
        )
    }

    @Test
    fun binaryStarOrbitStaysBound() {
        val m1 = 1.989e30
        val m2 = 1.989e30
        val separation = 1.496e8

        val star1 = TestBody(unit(m1, Kilogram), unit(7.0e5, Kilometer), Vec2(-separation * 0.5, 0.0))
        val star2 = TestBody(unit(m2, Kilogram), unit(7.0e5, Kilometer), Vec2(separation * 0.5, 0.0))

        setBinaryStarOrbit(star1, star2)

        val totalMass = m1 + m2
        val period = 2.0 * PI * sqrt(separation * separation * separation / (G * totalMass))
        val steps = 20_000
        val engine = PhysicsEngine(mutableListOf<GravityObject>(star1, star2))
        engine.rebuild()

        repeat(steps) { engine.step(period / steps) }
        engine.syncToObjects()

        val actualSeparation = (star2.position - star1.position).length()
        assertTrue(
            abs(actualSeparation - separation) / separation < 0.02,
            "Binary stars should remain bound at separation ~$separation, got $actualSeparation"
        )
    }

    @Test
    fun circumbinaryOrbitStaysBound() {
        val m1 = 1.989e30
        val m2 = 1.989e30
        val binarySeparation = 1.496e8
        val planetR = binarySeparation * 3.5

        val star1 = TestBody(unit(m1, Kilogram), unit(7.0e5, Kilometer), Vec2(-binarySeparation * 0.5, 0.0))
        val star2 = TestBody(unit(m2, Kilogram), unit(7.0e5, Kilometer), Vec2(binarySeparation * 0.5, 0.0))
        setBinaryStarOrbit(star1, star2)

        val planet = TestBody(unit(5.972e24, Kilogram), unit(6.371e3, Kilometer), Vec2(0.0, planetR))
        setCircularOrbit(planet, listOf(star1, star2))

        val totalMass = m1 + m2
        val period = 2.0 * PI * sqrt(planetR * planetR * planetR / (G * totalMass))
        val steps = 25_000
        val engine = PhysicsEngine(mutableListOf<GravityObject>(star1, star2, planet))
        engine.rebuild()

        repeat(steps) { engine.step(period / steps) }
        engine.syncToObjects()

        val planetDist = planet.position.length()
        assertTrue(
            abs(planetDist - planetR) / planetR < 0.05,
            "Circumbinary planet should remain in stable orbit, expected ~$planetR, got $planetDist"
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

        val engine = PhysicsEngine(objects)
        engine.rebuild()
        engine.resolveCollisions()
        engine.syncToObjects()

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
        val engine = PhysicsEngine(objects)
        engine.rebuild()
        while (objects.size > 1 && steps < 10_000) {
            engine.step(600.0)
            engine.resolveCollisions()
            engine.syncToObjects()
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

    @Test
    fun largeSystemStaysFiniteAndBounded() {
        val n = 200
        val random = Random(12345L)
        val starMass = 1.0e30
        val bodies = mutableListOf<GravityObject>()
        bodies.add(TestBody(unit(starMass, Kilogram), unit(5.0e5, Kilometer)))

        for (i in 0 until n) {
            val r = 3.0e6 + random.nextDouble() * 6.0e6
            val angle = random.nextDouble(0.0, 2.0 * PI)
            val v = sqrt(G * starMass / r)
            bodies.add(
                TestBody(
                    unit(1.0e24, Kilogram),
                    unit(5.0e4, Kilometer),
                    Vec2(cos(angle) * r, sin(angle) * r),
                    Vec2(-sin(angle) * v, cos(angle) * v)
                )
            )
        }

        val engine = PhysicsEngine(bodies)
        engine.rebuild()

        val initialMomentumX = bodies.sumOf { it.mass.value * it.velocity.x }
        val initialMomentumY = bodies.sumOf { it.mass.value * it.velocity.y }

        repeat(300) {
            engine.step(120.0)
            engine.resolveCollisions()
            engine.syncToObjects()
        }

        for (b in bodies) {
            assertTrue(b.position.x.isFinite() && b.position.y.isFinite(), "Position must stay finite")
            assertTrue(b.velocity.x.isFinite() && b.velocity.y.isFinite(), "Velocity must stay finite")
            assertTrue(
                abs(b.position.x) <= MAP_HALF_KM && abs(b.position.y) <= MAP_HALF_KM,
                "Body must stay inside the map"
            )
        }

        val finalMomentumX = bodies.sumOf { it.mass.value * it.velocity.x }
        val finalMomentumY = bodies.sumOf { it.mass.value * it.velocity.y }
        val scale = maxOf(abs(initialMomentumX), abs(initialMomentumY), 1.0)
        assertTrue(
            abs(finalMomentumX - initialMomentumX) / scale < 0.05,
            "BH momentum drift should stay small on the X axis"
        )
        assertTrue(
            abs(finalMomentumY - initialMomentumY) / scale < 0.05,
            "BH momentum drift should stay small on the Y axis"
        )
    }

    @Test
    fun barnesHutMatchesNaiveForSmallTheta() {
        val n = 50
        val random = Random(12345L)
        val px = DoubleArray(n)
        val py = DoubleArray(n)
        val masses = DoubleArray(n)
        val radii = DoubleArray(n)

        for (i in 0 until n) {
            px[i] = random.nextDouble(0.0, 5.0e8)
            py[i] = random.nextDouble(0.0, 5.0e8)
            masses[i] = random.nextDouble(1.0e24, 1.0e30)
            radii[i] = random.nextDouble(5.0e3, 5.0e5)
        }

        val (naiveAx, naiveAy) = naiveAcceleration(px, py, masses, radii)

        val accAx = DoubleArray(n)
        val accAy = DoubleArray(n)
        val bh = BarnesHut()
        bh.build(px, py, masses, radii, n)
        bh.computeRange(px, py, masses, radii, accAx, accAy, 0, n, 0.3)

        for (i in 0 until n) {
            val magnitude = sqrt(naiveAx[i] * naiveAx[i] + naiveAy[i] * naiveAy[i])
            val dx = accAx[i] - naiveAx[i]
            val dy = accAy[i] - naiveAy[i]
            val error = sqrt(dx * dx + dy * dy) / magnitude
            assertTrue(
                error < 0.05,
                "BH acceleration for body $i should match naive (rel error $error)"
            )
        }
    }

    @Test
    fun bodyEscapesMapAndIsRemoved() {
        val star = TestBody(unit(1.0e30, Kilogram), unit(5.0e5, Kilometer))
        val runaway = TestBody(
            unit(1.0e24, Kilogram),
            unit(5.0e3, Kilometer),
            Vec2(49.0 * 1.496e8, 0.0),
            Vec2(1.0e6, 0.0)
        )
        val bodies = mutableListOf<GravityObject>(star, runaway)

        val engine = PhysicsEngine(bodies)
        engine.rebuild()

        repeat(200) {
            engine.step(600.0)
            engine.resolveCollisions()
        }

        assertTrue(bodies.contains(star), "Star must remain in the simulation")
        assertFalse(bodies.contains(runaway), "Runaway body must be removed at the map boundary")
        assertEquals(1, engine.count, "Engine count must drop when a body escapes")
        assertEquals(1, bodies.size, "Body list must shrink in lockstep")
    }

    private fun naiveAcceleration(
        px: DoubleArray,
        py: DoubleArray,
        masses: DoubleArray,
        radii: DoubleArray,
    ): Pair<DoubleArray, DoubleArray> {
        val n = px.size
        val ax = DoubleArray(n)
        val ay = DoubleArray(n)

        for (i in 0 until n) {
            val xi = px[i]
            val yi = py[i]
            val ri = radii[i]
            val mi = masses[i]

            for (j in i + 1 until n) {
                val dx = px[j] - xi
                val dy = py[j] - yi

                val softening = ri + radii[j]
                val distSq = dx * dx + dy * dy + softening * softening
                val invDist = 1.0 / sqrt(distSq)
                val invDist3 = invDist * invDist * invDist

                val force = G * invDist3
                val fA = force * masses[j]
                val fB = force * mi

                ax[i] += dx * fA
                ay[i] += dy * fA
                ax[j] -= dx * fB
                ay[j] -= dy * fB
            }
        }

        return ax to ay
    }

    @Test
    fun collidingClusterMergesToOne() {
        val bodies = mutableListOf<GravityObject>()
        for (i in 0 until 5) {
            bodies.add(
                TestBody(
                    unit(1.0e24, Kilogram),
                    unit(1.0e6, Kilometer),
                    Vec2(0.0, 0.0),
                    Vec2((i + 1) * 10.0, -(i + 1) * 5.0)
                )
            )
        }

        val momentumBeforeX = bodies.sumOf { it.mass.value * it.velocity.x }
        val momentumBeforeY = bodies.sumOf { it.mass.value * it.velocity.y }

        val engine = PhysicsEngine(bodies)
        engine.rebuild()
        engine.resolveCollisions()
        engine.syncToObjects()

        assertEquals(1, engine.count, "Overlapping cluster should merge to one body")
        assertEquals(1, bodies.size, "Shared body list must shrink in lockstep")
        val survivor = bodies.first()
        assertEquals(5.0e24, survivor.mass.value, 1.0e15, "Merged mass should be sum of masses")
        assertEquals(momentumBeforeX, survivor.velocity.x * survivor.mass.value, 1.0e15)
        assertEquals(momentumBeforeY, survivor.velocity.y * survivor.mass.value, 1.0e15)
    }
}

import lib.NameGenerator
import lib.Seed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NameGeneratorTest {

    @Test
    fun testGeneratedNamesNotEmpty() {
        val random = Random(42)
        repeat(50) {
            val name = NameGenerator.generateSystemName(random)
            assertTrue(name.isNotBlank(), "System name should not be blank")
        }
    }

    @Test
    fun testStarAndPlanetNamingSingleStar() {
        val systemName = "Synuefe XY-Z d1-42"
        val starName = NameGenerator.formatStarName(systemName, 0, 1)
        val planet1Name = NameGenerator.formatPlanetName(systemName, 0, 1)
        val planet2Name = NameGenerator.formatPlanetName(systemName, 1, 1)

        assertEquals("Synuefe XY-Z d1-42 A", starName)
        assertEquals("Synuefe XY-Z d1-42 1", planet1Name)
        assertEquals("Synuefe XY-Z d1-42 2", planet2Name)
    }

    @Test
    fun testStarAndPlanetNamingBinaryStars() {
        val systemName = "Col 285 Sector FG-Y d1-18"
        val starAName = NameGenerator.formatStarName(systemName, 0, 2)
        val starBName = NameGenerator.formatStarName(systemName, 1, 2)
        val planet1Name = NameGenerator.formatPlanetName(systemName, 0, 2)
        val planet2Name = NameGenerator.formatPlanetName(systemName, 1, 2)

        assertEquals("Col 285 Sector FG-Y d1-18 A", starAName)
        assertEquals("Col 285 Sector FG-Y d1-18 B", starBName)
        assertEquals("Col 285 Sector FG-Y d1-18 AB 1", planet1Name)
        assertEquals("Col 285 Sector FG-Y d1-18 AB 2", planet2Name)
    }

    @Test
    fun testSolarSystemPopulateWithEliteNames() {
        val system = SolarSystem(Seed(12345L))
        system.populate(5)

        assertTrue(system.name.isNotBlank())
        assertTrue(system.objects.isNotEmpty())

        val stars = system.objects.filterIsInstance<spaceObjects.Star>()
        val planets = system.objects.filterIsInstance<spaceObjects.CelestialBody>()

        assertEquals(5, planets.size)
        assertTrue(stars.isNotEmpty())

        for (star in stars) {
            assertTrue(star.name.startsWith(system.name))
        }

        for (planet in planets) {
            assertTrue(planet.name.startsWith(system.name))
        }
    }
}

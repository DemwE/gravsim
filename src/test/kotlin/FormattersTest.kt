import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ui.*
import units.EarthMass
import units.Kilogram
import units.Kilometer
import units.SolarMass
import units.convertTo
import units.unit

class FormattersTest {

    @Test
    fun testFormatSimulationTime() {
        assertEquals("00:00:00", formatSimulationTime(0.0))
        assertEquals("00:01:15", formatSimulationTime(75.0))
        assertEquals("01:00:00", formatSimulationTime(3600.0))
        assertEquals("2 d 03:04:05", formatSimulationTime(2 * 86400.0 + 3 * 3600.0 + 4 * 60.0 + 5.0))
        assertEquals("1 y 10 d 00:00:00", formatSimulationTime(365.25 * 86400.0 + 10 * 86400.0))
    }

    @Test
    fun testFormatTimeScale() {
        assertTrue(formatTimeScale(1.0).contains("1s = 1.00 s"))
        assertTrue(formatTimeScale(60.0).contains("1s = 1.0 min"))
        assertTrue(formatTimeScale(3600.0).contains("1s = 1.00 h"))
        assertTrue(formatTimeScale(86400.0).contains("1s = 1.00 d"))
        assertTrue(formatTimeScale(31557600.0).contains("1s = 1.00 y"))
    }

    @Test
    fun testFormatVelocity() {
        assertEquals("29.78 km/s", formatVelocity(29.784))
        assertEquals("0.00 km/s", formatVelocity(0.0))
    }

    @Test
    fun testFormatMass() {
        val sunMass = unit(1.0, SolarMass).convertTo(Kilogram)
        val earthMass = unit(1.0, EarthMass).convertTo(Kilogram)

        assertTrue(formatMass(sunMass).contains("1.000 M_Sun"))
        assertTrue(formatMass(earthMass).contains("1.000 M_Earth"))
    }

    @Test
    fun testFormatDistance() {
        assertEquals("100 km", formatDistance(100.0))
        assertTrue(formatDistance(1.496e8).contains("1.000 AU"))
    }

    @Test
    fun testFormatRadius() {
        val starRadius = unit(696340.0, Kilometer)
        val planetRadius = unit(6371.0, Kilometer)
        assertTrue(formatRadius(starRadius).contains("1.00 R_Sun"))
        assertTrue(formatRadius(planetRadius).contains("1.00 R_Earth"))
    }
}

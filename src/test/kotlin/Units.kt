import org.junit.jupiter.api.Test
import lib.*
import units.*

class UnitsTest {

    @Test
    fun distance() {
        val distance = unit(2.0, AstronomicalUnit)
        println(distance.format())
        println(distance.convertTo(Kilometer).format())
        println(distance.convertTo(LightYear).format(12))
    }

    @Test
    fun mass() {
        val mass = unit(1.0, EarthMass)
        println(mass.format())
        println(mass.convertTo(Kilogram).format())
    }

    @Test
    fun temperature() {
        val temperature = unit(100.0, Kelvin)
        println(temperature.format())
        println(temperature.convertTo(Celsius).format())
        println(temperature.convertTo(Fahrenheit).format())
    }

    @Test
    fun range() {
        val massRange = Range(unit(0.1, EarthMass), unit(10.0, EarthMass))
        println(massRange.format())
    }
}
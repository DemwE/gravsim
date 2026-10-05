import org.junit.jupiter.api.Test
import lib.*
import units.*

class UnitsTest {

    @Test
    fun au() {
        val AU = AstronomicalUnit(8.1291281)
        println(AU.name)
        println(AU.format())
        println("AU to km: " + AU.toKiloMeters().format())
    }

    @Test
    fun em() {
        val EM = EarthMass(2.2)
        println(EM.name)
        println(EM.format())
        println("EM to SM: " + EM.toSunMass().format())
        println("EM to km: " + EM.toKilograms().format(0))
    }

    @Test
    fun sm() {
        val SM = SunMass(1.1)
        println(SM.name)
        println(SM.format())
        println("SM to EM: " + SM.toEarthMass().format())
        println("SM to kg: " + SM.toKilograms().format(0))
    }

    @Test
    fun range() {
        val RG = Range(EarthMass(1.2), SunMass(10.9).toEarthMass())
        println(RG.format())
    }
}
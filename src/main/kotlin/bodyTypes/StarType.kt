package bodyTypes

import com.raylib.Raylib.Color
import lib.Range
import units.*

enum class StarType(
    val displayName: String,
    val massRange: Range<SolarMass>,
    val radiusRange: Range<Kilometer>,
    val temperatureRange: Range<Kelvin>,
    val weight: Float,
    val color: Color,
) {
    // types are based on: https://elite-dangerous.fandom.com/wiki/Stars
    // MAIN SEQUENCE STARS (~82% of all stars when considering Brown Dwarfs)

    // Very Common (~46% of Main Sequence)
    M(
        displayName = "Class M Star",
        massRange = Range(unit(0.08, SolarMass), unit(0.45, SolarMass)),
        radiusRange = Range(unit(100000.0, Kilometer), unit(500000.0, Kilometer)),
        temperatureRange = Range(unit(2400.0, Kelvin), unit(3700.0, Kelvin)),
        weight = 39.1f,
        color = color(255, 110, 45),
    ),

    // Common (~25% of Main Sequence)
    K(
        displayName = "Class K Star",
        massRange = Range(unit(0.45, SolarMass), unit(0.8, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(700000.0, Kilometer)),
        temperatureRange = Range(unit(3700.0, Kelvin), unit(5200.0, Kelvin)),
        weight = 21.25f,
        color = color(255, 170, 80),
    ),

    // Common (~12% of Main Sequence)
    F(
        displayName = "Class F Star",
        massRange = Range(unit(1.0, SolarMass), unit(1.4, SolarMass)),
        radiusRange = Range(unit(800000.0, Kilometer), unit(1000000.0, Kilometer)),
        temperatureRange = Range(unit(6000.0, Kelvin), unit(7600.0, Kelvin)),
        weight = 10.2f,
        color = color(255, 244, 214),
    ),

    // Common (~9% of Main Sequence)
    G(
        displayName = "Class G Star",
        massRange = Range(unit(0.8, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(700000.0, Kilometer), unit(850000.0, Kilometer)),
        temperatureRange = Range(unit(5200.0, Kelvin), unit(6000.0, Kelvin)),
        weight = 7.65f,
        color = color(255, 220, 140),
    ),

    // Uncommon (~6% of Main Sequence)
    A(
        displayName = "Class A Star",
        massRange = Range(unit(1.4, SolarMass), unit(2.1, SolarMass)),
        radiusRange = Range(unit(1000000.0, Kilometer), unit(1500000.0, Kilometer)),
        temperatureRange = Range(unit(7600.0, Kelvin), unit(10000.0, Kelvin)),
        weight = 5.1f,
        color = color(255, 250, 240),
    ),

    // Rare (~1.7% of Main Sequence)
    B(
        displayName = "Class B Star",
        massRange = Range(unit(2.0, SolarMass), unit(16.0, SolarMass)),
        radiusRange = Range(unit(1500000.0, Kilometer), unit(5000000.0, Kilometer)),
        temperatureRange = Range(unit(10000.0, Kelvin), unit(30000.0, Kelvin)),
        weight = 1.445f,
        color = color(190, 205, 255),
    ),

    // Very Rare (~0.2% of Main Sequence)
    O(
        displayName = "Class O Star",
        massRange = Range(unit(15.0, SolarMass), unit(90.0, SolarMass)),
        radiusRange = Range(unit(4000000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(30000.0, Kelvin), unit(52000.0, Kelvin)),
        weight = 0.17f,
        color = color(157, 180, 255),
    ),

    // BROWN DWARFS (~15.0% global weight)

    // Very Common within Brown Dwarfs (~58% of 15%)
    L(
        displayName = "Class L Brown Dwarf",
        massRange = Range(unit(0.05, SolarMass), unit(0.08, SolarMass)),
        radiusRange = Range(unit(70000.0, Kilometer), unit(120000.0, Kilometer)),
        temperatureRange = Range(unit(1300.0, Kelvin), unit(2400.0, Kelvin)),
        weight = 8.7f,
        color = color(190, 60, 45),
    ),

    // Common within Brown Dwarfs (~27% of 15%)
    T(
        displayName = "Class T Brown Dwarf",
        massRange = Range(unit(0.02, SolarMass), unit(0.06, SolarMass)),
        radiusRange = Range(unit(60000.0, Kilometer), unit(100000.0, Kilometer)),
        temperatureRange = Range(unit(700.0, Kelvin), unit(1300.0, Kelvin)),
        weight = 4.05f,
        color = color(220, 95, 75),
    ),

    // Common within Brown Dwarfs (~15% of 15%)
    Y(
        displayName = "Class Y Brown Dwarf",
        massRange = Range(unit(0.005, SolarMass), unit(0.03, SolarMass)),
        radiusRange = Range(unit(30000.0, Kilometer), unit(80000.0, Kilometer)),
        temperatureRange = Range(unit(300.0, Kelvin), unit(700.0, Kelvin)),
        weight = 2.25f,
        color = color(120, 45, 55),
    ),

    // WHITE DWARFS (~0.36% global weight)

    // Very Common White Dwarf (~44% of 0.36%)
    DC(
        displayName = "Class DC White Dwarf",
        massRange = Range(unit(0.4, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(4000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(4000.0, Kelvin), unit(10000.0, Kelvin)),
        weight = 0.1584f,
        color = color(120, 45, 55),
    ),

    // Common White Dwarf (~29% of 0.36%)
    DA(
        displayName = "Class DA White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.3, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(15000.0, Kilometer)),
        temperatureRange = Range(unit(8000.0, Kelvin), unit(40000.0, Kelvin)),
        weight = 0.1044f,
        color = color(255, 250, 240),
    ),

    // Common White Dwarf (~12% of 0.36%)
    DAB(
        displayName = "Class DAB White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(14000.0, Kilometer)),
        temperatureRange = Range(unit(10000.0, Kelvin), unit(30000.0, Kelvin)),
        weight = 0.0432f,
        color = color(255, 250, 240),
    ),

    // Uncommon White Dwarf (~5.1% of 0.36%)
    DB(
        displayName = "Class DB White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(13000.0, Kilometer)),
        temperatureRange = Range(unit(12000.0, Kelvin), unit(30000.0, Kelvin)),
        weight = 0.01836f,
        color = color(190, 205, 255),
    ),

    // Rare White Dwarf (~3.8% of 0.36%)
    DCV(
        displayName = "Class DCV White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.1, SolarMass)),
        radiusRange = Range(unit(4000.0, Kilometer), unit(11000.0, Kilometer)),
        temperatureRange = Range(unit(4000.0, Kelvin), unit(12000.0, Kelvin)),
        weight = 0.01368f,
        color = color(220, 95, 75),
    ),

    // Rare White Dwarf (~3.2% of 0.36%)
    DAV(
        displayName = "Class DAV White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(10000.0, Kelvin), unit(13000.0, Kelvin)),
        weight = 0.01152f,
        color = color(255, 250, 240),
    ),

    // Rare White Dwarf (~1.0% of 0.36%)
    DBV(
        displayName = "Class DBV White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(22000.0, Kelvin), unit(29000.0, Kelvin)),
        weight = 0.0036f,
        color = color(190, 205, 255),
    ),

    // Very Rare White Dwarf (~0.9% of 0.36%)
    DD(
        displayName = "Class DD White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.3, SolarMass)),
        radiusRange = Range(unit(4000.0, Kilometer), unit(15000.0, Kilometer)),
        temperatureRange = Range(unit(8000.0, Kelvin), unit(40000.0, Kelvin)),
        weight = 0.00324f,
        color = color(255, 250, 240),
    ),

    // Very Rare White Dwarf (~0.43% of 0.36%)
    DAZ(
        displayName = "Class DAZ White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(7000.0, Kelvin), unit(20000.0, Kelvin)),
        weight = 0.001548f,
        color = color(220, 95, 75),
    ),

    // Very Rare White Dwarf (~0.11% of 0.36%)
    DBZ(
        displayName = "Class DBZ White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(10000.0, Kelvin), unit(25000.0, Kelvin)),
        weight = 0.000396f,
        color = color(190, 205, 255),
    ),

    // Extremely Rare White Dwarf (~0.02% of 0.36%)
    DQ(
        displayName = "Class DQ White Dwarf",
        massRange = Range(unit(0.5, SolarMass), unit(1.2, SolarMass)),
        radiusRange = Range(unit(5000.0, Kilometer), unit(12000.0, Kilometer)),
        temperatureRange = Range(unit(6000.0, Kelvin), unit(10000.0, Kelvin)),
        weight = 0.000072f,
        color = color(255, 244, 214),
    ),

    // PROTO STARS (~2.4% global weight)

    TTS(
        displayName = "T Tauri Star",
        massRange = Range(unit(0.1, SolarMass), unit(2.5, SolarMass)),
        radiusRange = Range(unit(700000.0, Kilometer), unit(3000000.0, Kilometer)),
        temperatureRange = Range(unit(3000.0, Kelvin), unit(7000.0, Kelvin)),
        weight = 2.304f,
        color = color(220, 95, 75),
    ),

    HERBIG_AE_BE(
        displayName = "Herbig Ae/Be Star",
        massRange = Range(unit(2.0, SolarMass), unit(8.0, SolarMass)),
        radiusRange = Range(unit(1500000.0, Kilometer), unit(5000000.0, Kilometer)),
        temperatureRange = Range(unit(8000.0, Kelvin), unit(30000.0, Kelvin)),
        weight = 0.096f,
        color = color(190, 205, 255),
    ),

    // GIANTS AND SUPERGIANTS (~0.25% global weight)

    M_RED_GIANT(
        displayName = "Class M Red Giant",
        massRange = Range(unit(0.3, SolarMass), unit(8.0, SolarMass)),
        radiusRange = Range(unit(10000000.0, Kilometer), unit(100000000.0, Kilometer)),
        temperatureRange = Range(unit(2500.0, Kelvin), unit(3500.0, Kelvin)),
        weight = 0.16f,
        color = color(255, 110, 45),
    ),

    K_ORANGE_GIANT(
        displayName = "Class K Orange Giant",
        massRange = Range(unit(0.8, SolarMass), unit(5.0, SolarMass)),
        radiusRange = Range(unit(5000000.0, Kilometer), unit(30000000.0, Kilometer)),
        temperatureRange = Range(unit(3700.0, Kelvin), unit(5000.0, Kelvin)),
        weight = 0.05f,
        color = color(255, 170, 80),
    ),

    A_BLUE_WHITE_SUPERGIANT(
        displayName = "Class A Blue-White Supergiant",
        massRange = Range(unit(10.0, SolarMass), unit(30.0, SolarMass)),
        radiusRange = Range(unit(20000000.0, Kilometer), unit(100000000.0, Kilometer)),
        temperatureRange = Range(unit(7500.0, Kelvin), unit(10000.0, Kelvin)),
        weight = 0.023f,
        color = color(255, 250, 240),
    ),

    F_WHITE_SUPERGIANT(
        displayName = "Class F White Supergiant",
        massRange = Range(unit(10.0, SolarMass), unit(25.0, SolarMass)),
        radiusRange = Range(unit(30000000.0, Kilometer), unit(150000000.0, Kilometer)),
        temperatureRange = Range(unit(6000.0, Kelvin), unit(7500.0, Kelvin)),
        weight = 0.00525f,
        color = color(255, 244, 214),
    ),

    G_WHITE_YELLOW_SUPERGIANT(
        displayName = "Class G White-Yellow Supergiant",
        massRange = Range(unit(10.0, SolarMass), unit(20.0, SolarMass)),
        radiusRange = Range(unit(40000000.0, Kilometer), unit(200000000.0, Kilometer)),
        temperatureRange = Range(unit(5000.0, Kelvin), unit(6000.0, Kelvin)),
        weight = 0.004f,
        color = color(255, 220, 140),
    ),

    M_RED_SUPERGIANT(
        displayName = "Class M Red Supergiant",
        massRange = Range(unit(10.0, SolarMass), unit(40.0, SolarMass)),
        radiusRange = Range(unit(100000000.0, Kilometer), unit(1000000000.0, Kilometer)),
        temperatureRange = Range(unit(3500.0, Kelvin), unit(4500.0, Kelvin)),
        weight = 0.0035f,
        color = color(255, 110, 45),
    ),

    B_BLUE_WHITE_SUPERGIANT(
        displayName = "Class B Blue-White Supergiant",
        massRange = Range(unit(15.0, SolarMass), unit(50.0, SolarMass)),
        radiusRange = Range(unit(15000000.0, Kilometer), unit(80000000.0, Kilometer)),
        temperatureRange = Range(unit(10000.0, Kelvin), unit(30000.0, Kelvin)),
        weight = 0.00175f,
        color = color(190, 205, 255),
    ),

    // CARBON STARS (~0.08% global weight)

    S(
        displayName = "Class S Star",
        massRange = Range(unit(0.8, SolarMass), unit(4.0, SolarMass)),
        radiusRange = Range(unit(5000000.0, Kilometer), unit(50000000.0, Kilometer)),
        temperatureRange = Range(unit(2300.0, Kelvin), unit(3500.0, Kelvin)),
        weight = 0.0296f,
        color = color(190, 60, 45),
    ),

    MS(
        displayName = "Class MS Star",
        massRange = Range(unit(0.8, SolarMass), unit(4.0, SolarMass)),
        radiusRange = Range(unit(5000000.0, Kilometer), unit(50000000.0, Kilometer)),
        temperatureRange = Range(unit(2300.0, Kelvin), unit(3500.0, Kelvin)),
        weight = 0.0288f,
        color = color(190, 60, 45),
    ),

    CN(
        displayName = "Class C-N Carbon Star",
        massRange = Range(unit(1.0, SolarMass), unit(3.0, SolarMass)),
        radiusRange = Range(unit(10000000.0, Kilometer), unit(70000000.0, Kilometer)),
        temperatureRange = Range(unit(2500.0, Kelvin), unit(3200.0, Kelvin)),
        weight = 0.0192f,
        color = color(190, 60, 45),
    ),

    CJ(
        displayName = "Class C-J Carbon Star",
        massRange = Range(unit(1.0, SolarMass), unit(3.0, SolarMass)),
        radiusRange = Range(unit(10000000.0, Kilometer), unit(70000000.0, Kilometer)),
        temperatureRange = Range(unit(2500.0, Kelvin), unit(3200.0, Kelvin)),
        weight = 0.0024f,
        color = color(190, 60, 45),
    ),

    C(
        displayName = "Class C Carbon Star",
        massRange = Range(unit(1.0, SolarMass), unit(5.0, SolarMass)),
        radiusRange = Range(unit(10000000.0, Kilometer), unit(80000000.0, Kilometer)),
        temperatureRange = Range(unit(2000.0, Kelvin), unit(4300.0, Kelvin)),
        weight = 0.00056f,
        color = color(190, 60, 45),
    ),

    CH(
        displayName = "Class CH Carbon Star",
        massRange = Range(unit(0.8, SolarMass), unit(2.0, SolarMass)),
        radiusRange = Range(unit(5000000.0, Kilometer), unit(40000000.0, Kilometer)),
        temperatureRange = Range(unit(3000.0, Kelvin), unit(4300.0, Kelvin)),
        weight = 0.00024f,
        color = color(190, 60, 45),
    ),

    CHD(
        displayName = "Class CHd Carbon Star",
        massRange = Range(unit(0.8, SolarMass), unit(2.0, SolarMass)),
        radiusRange = Range(unit(5000000.0, Kilometer), unit(40000000.0, Kilometer)),
        temperatureRange = Range(unit(3000.0, Kelvin), unit(4300.0, Kelvin)),
        weight = 0.00016f,
        color = color(190, 60, 45),
    ),

    CS(
        displayName = "Class CS Carbon Star",
        massRange = Range(unit(1.0, SolarMass), unit(4.0, SolarMass)),
        radiusRange = Range(unit(10000000.0, Kilometer), unit(60000000.0, Kilometer)),
        temperatureRange = Range(unit(2300.0, Kelvin), unit(3800.0, Kelvin)),
        weight = 0.0001f,
        color = color(190, 60, 45),
    ),

    // WOLF-RAYET STARS (~0.05% global weight)

    WO(
        displayName = "Wolf-Rayet WO Star",
        massRange = Range(unit(10.0, SolarMass), unit(60.0, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(60000.0, Kelvin), unit(200000.0, Kelvin)),
        weight = 0.0235f,
        color = color(157, 180, 255),
    ),

    WC(
        displayName = "Wolf-Rayet WC Star",
        massRange = Range(unit(10.0, SolarMass), unit(50.0, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(50000.0, Kelvin), unit(150000.0, Kelvin)),
        weight = 0.0105f,
        color = color(157, 180, 255),
    ),

    WNC(
        displayName = "Wolf-Rayet WNC Star",
        massRange = Range(unit(15.0, SolarMass), unit(55.0, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(50000.0, Kelvin), unit(160000.0, Kelvin)),
        weight = 0.008f,
        color = color(157, 180, 255),
    ),

    WN(
        displayName = "Wolf-Rayet WN Star",
        massRange = Range(unit(10.0, SolarMass), unit(50.0, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(30000.0, Kelvin), unit(140000.0, Kelvin)),
        weight = 0.0075f,
        color = color(157, 180, 255),
    ),

    W(
        displayName = "Wolf-Rayet W Star",
        massRange = Range(unit(20.0, SolarMass), unit(80.0, SolarMass)),
        radiusRange = Range(unit(500000.0, Kilometer), unit(10000000.0, Kilometer)),
        temperatureRange = Range(unit(30000.0, Kelvin), unit(200000.0, Kelvin)),
        weight = 0.00022f,
        color = color(157, 180, 255),
    );
}
package bodyTypes

import com.raylib.Raylib.Color

object StarColors {
    fun star(type: StarType): Color = when (type) {
        StarType.O, StarType.WO, StarType.WC, StarType.WNC, StarType.WN, StarType.W ->
            color(157, 180, 255)

        StarType.B, StarType.B_BLUE_WHITE_SUPERGIANT, StarType.HERBIG_AE_BE,
        StarType.DB, StarType.DBV, StarType.DBZ ->
            color(190, 205, 255)

        StarType.A, StarType.A_BLUE_WHITE_SUPERGIANT, StarType.DA, StarType.DAB,
        StarType.DAV, StarType.DD ->
            color(255, 250, 240)

        StarType.F, StarType.F_WHITE_SUPERGIANT, StarType.DQ ->
            color(255, 244, 214)

        StarType.G, StarType.G_WHITE_YELLOW_SUPERGIANT ->
            color(255, 220, 140)

        StarType.K, StarType.K_ORANGE_GIANT ->
            color(255, 170, 80)

        StarType.M, StarType.M_RED_GIANT, StarType.M_RED_SUPERGIANT ->
            color(255, 110, 45)

        StarType.L, StarType.S, StarType.MS, StarType.CN, StarType.CJ,
        StarType.C, StarType.CH, StarType.CHD, StarType.CS ->
            color(190, 60, 45)

        StarType.T, StarType.TTS, StarType.DCV, StarType.DAZ ->
            color(220, 95, 75)

        StarType.Y, StarType.DC ->
            color(120, 45, 55)
    }

    private fun color(red: Int, green: Int, blue: Int): Color =
        Color()
            .r(red.toByte())
            .g(green.toByte())
            .b(blue.toByte())
            .a(255.toByte())
}

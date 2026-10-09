package bodyTypes

import com.raylib.Raylib.Color

/**
 * Tworzy instancję [Color] z biblioteki Raylib dla podanych składowych RGB i opcjonalnej przezroczystości Alpha.
 */
fun color(red: Int, green: Int, blue: Int, alpha: Int = 255): Color =
    Color()
        .r(red.toByte())
        .g(green.toByte())
        .b(blue.toByte())
        .a(alpha.toByte())

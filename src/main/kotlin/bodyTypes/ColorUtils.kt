package bodyTypes

import com.raylib.Raylib.Color

fun color(red: Int, green: Int, blue: Int, alpha: Int = 255): Color =
    Color()
        .r(red.toByte())
        .g(green.toByte())
        .b(blue.toByte())
        .a(alpha.toByte())

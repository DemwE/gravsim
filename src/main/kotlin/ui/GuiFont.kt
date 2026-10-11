package ui

import com.raylib.Raylib.*
import lib.ResourceLoader
import java.io.File

object GuiFont {
    private var customFont: Font? = null
    private var initialized = false
    private var tempFontFile: File? = null

    private fun ensureLoaded() {
        if (initialized) return
        initialized = true

        try {
            val tempFile = ResourceLoader.extractToTempFile("fonts/Anta-Regular.ttf", "gravsim_font_", ".ttf")
            tempFontFile = tempFile
            val glyphs = ((32..126) + 0x00B7).toIntArray()
            val loaded = LoadFontEx(tempFile.absolutePath, 48, glyphs, glyphs.size)
            if (loaded.texture().id() != 0) {
                SetTextureFilter(loaded.texture(), TEXTURE_FILTER_BILINEAR)
                customFont = loaded
            }
        } catch (_: Exception) {
            // Keep default font if loading fails
        }
    }

    fun font(): Font {
        ensureLoaded()
        return customFont ?: GetFontDefault()
    }

    fun drawText(text: String, x: Float, y: Float, fontSize: Float, color: Color, spacing: Float = 0.5f) {
        val position = Vector2().x(x).y(y)
        DrawTextEx(font(), text, position, fontSize, spacing, color)
    }

    fun drawText(text: String, x: Int, y: Int, fontSize: Int, color: Color, spacing: Float = 0.5f) {
        drawText(text, x.toFloat(), y.toFloat(), fontSize.toFloat(), color, spacing)
    }

    fun measureTextWidth(text: String, fontSize: Float, spacing: Float = 0.5f): Float {
        return MeasureTextEx(font(), text, fontSize, spacing).x()
    }

    fun unload() {
        customFont?.let {
            if (it.texture().id() != 0) {
                UnloadFont(it)
            }
        }
        customFont = null
        initialized = false
        tempFontFile?.delete()
        tempFontFile = null
    }
}

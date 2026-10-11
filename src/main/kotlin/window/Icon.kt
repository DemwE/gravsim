package window

import com.raylib.Raylib.Image
import com.raylib.Raylib.LoadImage
import lib.ResourceLoader
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

fun loadWindowIcon(): Image {
    val sourceFile = ResourceLoader.extractToTempFile("icon.png", "gravsim_icon_source_", ".png")
    val sourceImage = ImageIO.read(sourceFile) ?: error("Could not decode icon.png")
    val correctedImage = BufferedImage(sourceImage.width, sourceImage.height, BufferedImage.TYPE_INT_ARGB)

    for (y in 0 until sourceImage.height) {
        for (x in 0 until sourceImage.width) {
            val argb = sourceImage.getRGB(x, y)
            val alpha = argb ushr 24 and 0xFF
            val red = argb ushr 16 and 0xFF
            val green = argb ushr 8 and 0xFF
            val blue = argb and 0xFF
            // Raylib's SDL backend applies channel masks for BGRA data to this RGBA image.
            correctedImage.setRGB(x, y, (red shl 24) or (alpha shl 16) or (blue shl 8) or green)
        }
    }

    val correctedFile = java.nio.file.Files.createTempFile("gravsim_icon_", ".png").toFile()
    correctedFile.deleteOnExit()
    check(ImageIO.write(correctedImage, "png", correctedFile)) { "Could not encode corrected window icon" }
    return LoadImage(correctedFile.absolutePath)
}
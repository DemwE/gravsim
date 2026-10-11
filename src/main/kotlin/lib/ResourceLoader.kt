package lib

import java.io.File
import java.io.InputStream
import java.nio.file.Files

object ResourceLoader {
    fun getResourceAsStream(resourcePath: String): InputStream {
        val normalizedPath = resourcePath.trimStart('/')
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(normalizedPath)
            ?: ClassLoader.getSystemResourceAsStream(normalizedPath)

        require(stream != null) { "Missing resource: $normalizedPath" }
        return stream
    }

    fun extractToTempFile(resourcePath: String, prefix: String, suffix: String): File {
        val tempFile = Files.createTempFile(prefix, suffix).toFile()
        tempFile.deleteOnExit()

        getResourceAsStream(resourcePath).use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}

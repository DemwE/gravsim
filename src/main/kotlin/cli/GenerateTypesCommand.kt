package cli

import bodyTypes.AtmosphereType
import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import com.github.ajalt.clikt.core.CliktCommand

class GenerateTypesCommand : CliktCommand(name = "generate-types") {
    override fun run() {
        echo("StarType:")
        echo(StarType.entries.joinToString(", ") { it.name })
        echo()
        echo("CelestialBodyType:")
        echo(CelestialBodyType.entries.joinToString(", ") { it.name })
        echo()
        echo("AtmosphereType:")
        echo(AtmosphereType.entries.joinToString(", ") { it.name })
    }
}

package cli

import SimulationRunner
import presets.SystemPreset
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import engine.MAX_PLANETS
import lib.Seed
import SolarSystemGenerator

class SimulationCommand : CliktCommand(name = "gravsim") {
    private val preset by option("--preset", help = "System preset")
        .choice(*SystemPreset.entries.map { it.argument to it }.toTypedArray())
        .default(SystemPreset.RANDOM)
    private val seed by option("--seed", help = "Seed for a repeatable random system")
        .long()
    private val planets by option("--planets", help = "Override seed-generated count (1-$MAX_PLANETS)")
        .int()
        .check("must be between 1 and $MAX_PLANETS") { it in 1..MAX_PLANETS }

    override fun run() {
        val actualSeed = seed ?: Seed(null).value
        val actualPlanetCount = planets ?: SolarSystemGenerator.planetCountFromSeed(Seed(actualSeed))
        echo("Seed: $actualSeed")
        if (preset == SystemPreset.RANDOM) echo("Planets: $actualPlanetCount")
        SimulationRunner.run(preset, actualSeed, planets)
    }
}

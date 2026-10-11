package cli

import SimulationRunner
import presets.*
import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.*
import engine.MAX_PLANETS
import lib.Seed
import SolarSystemGenerator
import java.io.File

class SimulationCommand : CliktCommand(name = "gravsim") {
    init {
        subcommands(GenerateTypesCommand())
    }

    override val invokeWithoutSubcommand = true

    private val preset by option("--preset", help = "System preset")
        .choice(*SystemPreset.entries.map { it.argument to it }.toTypedArray())
    private val seed by option("--seed", help = "Seed for a repeatable random system")
        .long()
    private val planets by option("--planets", help = "Override seed-generated count (1-$MAX_PLANETS)")
        .int()
        .check("must be between 1 and $MAX_PLANETS") { it in 1..MAX_PLANETS }
    private val presetFile by option("--preset-file", help = "Load a custom system preset from a JSON file")

    override fun run() {
        if (currentContext.invokedSubcommand != null) return

        if (presetFile != null) {
            if (preset != null) {
                throw UsageError("Use either --preset or --preset-file, not both")
            }
            if (seed != null) {
                throw UsageError("Do not combine --seed with --preset-file")
            }
            if (planets != null) {
                throw UsageError("Planet count is defined by the JSON preset; do not pass --planets")
            }
            if (seed != null) {
                throw UsageError("--seed is only used by generated presets and cannot be combined with --preset-file")
            }
            val customPreset = try {
                JsonSystemPreset.load(File(presetFile!!))
            } catch (error: Exception) {
                throw UsageError("Could not load preset file: ${error.message}")
            }
            echo("Preset: ${customPreset.systemName}")
            SimulationRunner.run(customPreset, Seed(null).value, null)
            return
        }

        if (preset != null && seed != null) {
            throw UsageError("Do not combine --seed with --preset; use --seed alone for a generated random system")
        }

        val selectedPreset = preset ?: SystemPreset.RANDOM
        val actualSeed = seed ?: Seed(null).value
        val actualPlanetCount = planets ?: SolarSystemGenerator.planetCountFromSeed(Seed(actualSeed))
        echo("Seed: $actualSeed")
        if (selectedPreset == SystemPreset.RANDOM) echo("Planets: $actualPlanetCount")
        SimulationRunner.run(selectedPreset, actualSeed, planets)
    }
}

package presets

import SolarSystem
import lib.Seed

internal object RandomSystemPreset : PresetFactory {
    override fun create(seed: Seed, planets: Int?): SolarSystem =
        SolarSystem(seed).apply { populate(planets) }
}

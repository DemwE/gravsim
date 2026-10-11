package presets

import lib.Seed

enum class SystemPreset(
    val argument: String,
    private val factory: PresetFactory,
) : PresetFactory {
    RANDOM("random", RandomSystemPreset),
    SOLAR_SYSTEM("solar-system", SolarSystemPreset),
    PROXIMA_CENTAURI("proxima-centauri", ProximaCentauriPreset);

    override fun create(seed: Seed, planets: Int?) = factory.create(seed, planets)
}

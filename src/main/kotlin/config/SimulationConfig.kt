package config

/** Tunable values shared by simulation, camera, rendering, generation, and HUD. */
object SimulationConfig {
    const val TARGET_FPS = 75
    const val MAX_FRAME_SECONDS = 0.05f
}

object CameraConfig {
    const val INITIAL_ZOOM = 0.00001f
    const val MIN_ZOOM = 1.0e-8f
    const val MAX_ZOOM = 1000f
    const val WHEEL_ZOOM_STEP = 0.1f
    const val VISUAL_RADIUS_MULTIPLIER = 3.0
    const val MIN_VISUAL_MARGIN_PIXELS = 100.0
    const val OBJECT_PICK_PADDING_PIXELS = 25.0
}

object GenerationConfig {
    const val MIN_STARS = 1
    const val MAX_STARS_EXCLUSIVE = 3
    const val STAR_GAP_AU = 1.0
    const val PLANET_GAP_FRACTION_OF_STAR_GAP = 0.1
    const val MIN_BINARY_PLANET_ORBIT_FACTOR = 2.5
    const val MIN_STAR_SEPARATION_RADII = 3.0
    const val ORBIT_RADIUS_JITTER_FRACTION = 0.2
    const val SUGGESTED_STEPS_PER_ORBIT = 12.0
    const val MIN_ORBIT_MAP_FRACTION = 0.4
    const val MAX_ORBIT_MAP_FRACTION = 0.8
    const val DEFAULT_SUGGESTED_TIME_SCALE = 1.0e5
    const val SEED_PLANET_COUNT_SALT = 0x51A7E5L
}

object TrailConfig {
    const val DEFAULT_MAX_POINTS = 800
    const val DEFAULT_SEGMENT_KM = 1000.0
    const val DRAW_ALPHA_BANDS = 16
    const val STAR_TRAIL_ALPHA = 180
    const val PLANET_TRAIL_ALPHA = 200
}

object WorldRenderConfig {
    val RANGE_MARKERS_AU = listOf(10f to 0.10f, 25f to 0.14f, 50f to 0.18f, 100f to 0.25f)
    const val RANGE_RING_SEGMENTS = 256
    const val CELESTIAL_BODY_CIRCLE_SEGMENTS = 128
    const val MIN_BODY_RADIUS_PIXELS = 2f
    const val STAR_GLOW_RADIUS_MULTIPLIER = 2.5f
    const val ATMOSPHERE_RADIUS_MULTIPLIER = 1.5f
    const val ATMOSPHERE_OPACITY = 75
    const val BOUNDARY_ALPHA = 0.12f
    const val CORNER_BRACKET_FRACTION = 0.035f
    const val CORNER_BRACKET_ALPHA = 0.55f
    const val BOUNDARY_TICK_FRACTION = 0.012f
    const val BOUNDARY_TICK_ALPHA = 0.28f
    const val GRID_STEP_AU = 10f
    const val GRID_MAJOR_EVERY = 5
    const val GRID_MINOR_ALPHA = 0.035f
    const val GRID_MAJOR_ALPHA = 0.075f
    const val GRID_AXIS_ALPHA = 0.13f
}

object HudConfig {
    const val INITIAL_SCALE = 1.3f
    const val MIN_SCALE = 0.6f
    const val MAX_SCALE = 2.5f
    const val SCALE_STEP = 0.1f
    const val RESET_SCALE = 1.0f
    const val MIN_DPI_SCALE = 1.0f
    const val TOP_PANEL_WIDTH = 480f
    const val TOP_PANEL_HEIGHT = 100f
    const val TOP_PAUSE_WIDTH = 42f
    const val TOP_STEP_START_X = 68f
    const val TOP_STEP_WIDTH = 58f
    const val SYSTEM_PANEL_WIDTH = 340f
    const val SYSTEM_PANEL_HEIGHT = 150f
    const val INSPECTOR_WIDTH = 360f
    const val INSPECTOR_HEIGHT = 180f
    const val BOTTOM_BAR_HEIGHT = 32f
    const val PANEL_ACCENT_MIN_HEIGHT = 2
    const val PANEL_ACCENT_HEIGHT_DIVISOR = 45
    const val PANEL_ACCENT_ALPHA = 0.9f
    const val DIVIDER_ALPHA = 0.22f
}

import com.raylib.Colors.*
import com.raylib.Raylib.*
import lib.Seed
import java.time.Instant
import java.util.Locale

private const val TARGET_FPS = 60
private const val DEFAULT_PLANETS = 10
private const val MAX_FRAME_TIME = 0.05f

fun main() {
    SetTargetFPS(TARGET_FPS)
    initializeWindow()

    val camera = Camera2D()
        .offset(Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))
        .target(Vector2())
        .zoom(0.001f)
    val cameraController = CameraController()

    var system = SolarSystem("System", Seed(Instant.now().toEpochMilli()))
    system.populate(planets = DEFAULT_PLANETS)

    // Prędkość symulacji (sekundy symulacji na sekundę rzeczywistą) — niezależna od FPS.
    var timeScale = system.suggestedTimeScale
    var paused = false
    var simulatedTime = 0.0

    while (!WindowShouldClose()) {
        cameraController.update(camera)

        if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) timeScale *= 2.0
        if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) timeScale *= 0.5
        if (IsKeyPressed(KEY_ZERO)) timeScale = system.suggestedTimeScale
        if (IsKeyPressed(KEY_SPACE)) paused = !paused
        if (IsKeyPressed(KEY_R)) {
            system = SolarSystem("System", Seed(Instant.now().toEpochMilli()))
            system.populate(planets = DEFAULT_PLANETS)
            timeScale = system.suggestedTimeScale
            simulatedTime = 0.0
        }

        if (!paused) {
            val dtSim = GetFrameTime().coerceAtMost(MAX_FRAME_TIME).toDouble() * timeScale
            system.update(dtSim)
            simulatedTime += dtSim
        }

        BeginDrawing()
        ClearBackground(BLACK)
        BeginMode2D(camera)
        system.objects.forEach { it.draw() }
        EndMode2D()

        val status = String.format(
            Locale.US,
            "predkosc: %.3g x   czas symulacji: %.3g s%s",
            timeScale,
            simulatedTime,
            if (paused) "   [PAUZA]" else ""
        )
        DrawText(status, 20, 20, 20, RAYWHITE)
        DrawText("+/- predkosc   0 reset   spacja pauza   R nowy uklad", 20, 46, 20, GRAY)
        DrawFPS(20, 72)

        EndDrawing()
    }

    CloseWindow()
}

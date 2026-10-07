import com.raylib.Colors.*
import com.raylib.Raylib.*
import lib.Seed
import java.time.Instant

fun main() {
    SetTargetFPS(1024)
    initializeWindow()

    val camera = Camera2D()
        .offset(Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))
        .target(Vector2())
        .zoom(0.001f)
    val cameraController = CameraController()

    val system = SolarSystem("System", Seed(Instant.now().toEpochMilli()))
    system.populate(planets = 1)

    while (!WindowShouldClose()) {
        cameraController.update(camera)

        BeginDrawing()
        ClearBackground(BLACK);
        BeginMode2D(camera)
        system.update(GetFrameTime())
        system.objects.forEach { it.draw() }

        EndMode2D()
        DrawFPS(20, 20);
        EndDrawing()
    }

    CloseWindow()
}
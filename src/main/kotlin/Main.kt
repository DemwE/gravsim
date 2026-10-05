import com.raylib.Colors.*
import com.raylib.Raylib.*

fun main() {
    SetTargetFPS(60)
    initializeWindow()

    val camera = Camera3D()
        ._position(Vector3().x(18f).y(16f).z(18f))
        .target(Vector3())
        .up(Vector3().x(0f).y(1f).z(0f))
        .fovy(45f).projection(CAMERA_PERSPECTIVE)

    while (!WindowShouldClose()) {
        BeginDrawing()
        ClearBackground(BLACK);
        BeginMode3D(camera);
        DrawGrid(20, 1.0f);
        EndMode3D();
        DrawText("Hello world", 190, 200, 20, VIOLET);
        DrawFPS(20, 20);

        EndDrawing()
    }

    CloseWindow()
}
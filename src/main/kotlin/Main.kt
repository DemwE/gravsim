import com.raylib.Colors.*
import com.raylib.Raylib.*

fun main() {
    SetTargetFPS(60)
    initializeWindow()

    while (!WindowShouldClose()) {
        BeginDrawing()
        ClearBackground(BLACK)

        EndDrawing()
    }

    CloseWindow()
}
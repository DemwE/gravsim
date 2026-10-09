import com.raylib.Colors.*
import com.raylib.Raylib.*
import lib.Seed
import ui.GuiFont
import ui.SimulationHud
import java.time.Instant

private const val TARGET_FPS = 60
private const val DEFAULT_PLANETS = 10
private const val MAX_FRAME_TIME = 0.05f

fun main() {
    SetTargetFPS(TARGET_FPS)
    initializeWindow()

    val camera = Camera2D()
        .offset(Vector2().x(GetRenderWidth() / 2f).y(GetRenderHeight() / 2f))
        .target(Vector2())
        .zoom(0.00001f)
    val cameraController = CameraController()
    val hud = SimulationHud()

    var system = SolarSystem(Seed(Instant.now().toEpochMilli()))
    system.populate(planets = DEFAULT_PLANETS)

    var timeScale = system.suggestedTimeScale
    var paused = false
    var simulatedTime = 0.0

    while (!WindowShouldClose()) {
        cameraController.update(camera)

        val ctrlDown = IsKeyDown(KEY_LEFT_CONTROL) || IsKeyDown(KEY_RIGHT_CONTROL)

        if (ctrlDown) {
            if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) hud.scaleUp()
            if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) hud.scaleDown()
            if (IsKeyPressed(KEY_ZERO) || IsKeyPressed(KEY_KP_0) || IsKeyPressed(KEY_O)) hud.scaleReset()
        } else {
            if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) timeScale *= 2.0
            if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) timeScale *= 0.5
            if (IsKeyPressed(KEY_ZERO) || IsKeyPressed(KEY_KP_0) || IsKeyPressed(KEY_O)) {
                timeScale = system.suggestedTimeScale
            }
        }
        if (IsKeyPressed(KEY_SPACE)) paused = !paused

        if (IsKeyPressed(KEY_C)) {
            cameraController.reset(camera)
        }

        if (IsKeyPressed(KEY_TAB)) {
            hud.showOverlay = !hud.showOverlay
        }

        if (IsKeyPressed(KEY_F)) {
            val selected = hud.selectedObject
            if (selected != null) {
                if (cameraController.followedObject == selected) {
                    cameraController.follow(null)
                } else {
                    cameraController.follow(selected)
                }
            }
        }

        if (IsKeyPressed(KEY_ESCAPE)) {
            hud.selectedObject = null
            cameraController.follow(null)
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            val mousePos = GetMousePosition()
            val clickedObj = hud.selectObjectAt(mousePos, camera, system.objects)
            if (clickedObj != null) {
                hud.selectedObject = clickedObj
            }
        }

        if (IsKeyPressed(KEY_R)) {
            system = SolarSystem(Seed(Instant.now().toEpochMilli()))
            system.populate(planets = DEFAULT_PLANETS)
            timeScale = system.suggestedTimeScale
            simulatedTime = 0.0
            hud.selectedObject = null
            cameraController.reset(camera)
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

        hud.drawWorldOverlay(camera, system.objects, cameraController.followedObject)
        hud.drawScreenHud(timeScale, simulatedTime, paused, system.objects, cameraController.followedObject, system.name)

        EndDrawing()
    }

    GuiFont.unload()
    CloseWindow()
}

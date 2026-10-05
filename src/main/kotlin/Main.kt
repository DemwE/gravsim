import bodyTypes.AtmosphereType
import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.CelestialBody
import spaceObjects.Star
import units.*

fun main() {
    SetTargetFPS(60)
    initializeWindow()

    val camera = Camera2D()
        .offset(Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))
        .target(Vector2())
        .zoom(0.001f)
    val cameraController = CameraController()

    val sun = Star(
        name = "Sun",
        type = StarType.G,
        mass = unit(1.0, SolarMass),
        radius = unit(100340.0, Kilometer),
        temperature = unit(5778.0, Kelvin),
        position = Vector2()
    )

    val planet1 = CelestialBody(
        name = "Planet 1",
        type = CelestialBodyType.ROCKY_BODY,
        atmosphere = AtmosphereType.METHANE,
        mass = unit(0.5, EarthMass),
        radius = unit(3000.0, Kilometer),
        position = Vector2()
            .x(unit(0.12, AstronomicalUnit).convertToUnit(Kilometer).value.toFloat())
            .y(0f)
    )

    val planet2 = CelestialBody(
        name = "Planet 2",
        type = CelestialBodyType.ROCKY_BODY,
        atmosphere = AtmosphereType.OXYGEN,
        mass = unit(0.5, EarthMass),
        radius = unit(3000.0, Kilometer),
        position = Vector2()
            .x(unit(0.1, AstronomicalUnit).convertToUnit(Kilometer).value.toFloat())
            .y(0f)
    )

    while (!WindowShouldClose()) {
        cameraController.update(camera)

        BeginDrawing()
        ClearBackground(BLUE);
        BeginMode2D(camera)
        sun.draw()
        planet1.draw()
        planet2.draw()

        EndMode2D()
        DrawText("Hello world", 190, 200, 20, VIOLET);
        DrawFPS(20, 20);
        EndDrawing()
    }

    CloseWindow()
}
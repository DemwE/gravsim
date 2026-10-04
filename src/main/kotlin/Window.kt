import com.raylib.Raylib.*

private const val WINDOW_TITLE = "N-Body Simulation"
private const val MIN_WINDOW_WIDTH = 400
private const val MIN_WINDOW_HEIGHT = 300

fun initializeWindow() {
    InitWindow(1, 1, "Init")
    val monitor = GetCurrentMonitor()
    val screenWidth = GetMonitorWidth(monitor)
    val screenHeight = GetMonitorHeight(monitor)
    CloseWindow()

    SetConfigFlags(FLAG_WINDOW_RESIZABLE or FLAG_WINDOW_HIGHDPI)

    val windowWidth = screenWidth / 2
    val windowHeight = screenHeight / 2
    val posX = (screenWidth - windowWidth) / 2
    val posY = (screenHeight - windowHeight) / 2

    InitWindow(windowWidth, windowHeight, WINDOW_TITLE)
    SetWindowPosition(posX, posY)
    SetWindowMinSize(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)
}

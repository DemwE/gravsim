package spaceObjects

import com.raylib.Raylib.Vector2

interface Object {
    val name: String
    var position: Vector2

    fun draw()
}
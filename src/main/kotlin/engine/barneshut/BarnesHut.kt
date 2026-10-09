package engine.barneshut

import engine.QuadtreePool

class BarnesHut internal constructor(
    private val pool: QuadtreePool = QuadtreePool()
) {
    private val tree = BarnesHutTree(pool)
    private val calculator = BarnesHutForceCalculator(pool)

    fun build(px: DoubleArray, py: DoubleArray, masses: DoubleArray, radii: DoubleArray, n: Int) {
        tree.build(px, py, masses, radii, n)
    }

    fun computeRange(
        px: DoubleArray,
        py: DoubleArray,
        masses: DoubleArray,
        radii: DoubleArray,
        ax: DoubleArray,
        ay: DoubleArray,
        start: Int,
        end: Int,
        theta: Double,
    ) {
        calculator.computeRange(px, py, masses, radii, ax, ay, start, end, theta)
    }
}

package engine.barneshut

import engine.BARNES_HUT_MAX_DEPTH
import engine.MAP_HALF_KM
import engine.QuadtreePool

/**
 * 2D Barnes–Hut Quadtree builder.
 *
 * Recursively subdivides 2D space into quadrants to aggregate mass and center-of-mass.
 */
internal class BarnesHutTree(
    val pool: QuadtreePool
) {
    var pX: DoubleArray = DoubleArray(0)
        private set
    var pY: DoubleArray = DoubleArray(0)
        private set
    var pMass: DoubleArray = DoubleArray(0)
        private set
    var pRadius: DoubleArray = DoubleArray(0)
        private set

    fun build(px: DoubleArray, py: DoubleArray, masses: DoubleArray, radii: DoubleArray, n: Int) {
        if (n == 0) return
        pool.ensureCapacity(n)
        pool.reset()
        pX = px
        pY = py
        pMass = masses
        pRadius = radii
        pool.allocateNode(0.0, 0.0, MAP_HALF_KM)
        for (i in 0 until n) {
            insertBody(i, px[i], py[i], masses[i], radii[i], 0, 0)
        }
    }

    private fun quadrant(x: Double, y: Double, cx0: Double, cy0: Double): Int =
        (if (x >= cx0) 2 else 0) or (if (y >= cy0) 1 else 0)

    private fun childFor(node: Int, x: Double, y: Double): Int {
        val g = quadrant(x, y, pool.cx[node], pool.cy[node])
        val existing = pool.child(node, g)
        if (existing == QuadtreePool.EMPTY) {
            val half = pool.h[node] * 0.5
            val childCx = pool.cx[node] + if ((g and 2) != 0) half else -half
            val childCy = pool.cy[node] + if ((g and 1) != 0) half else -half
            val created = pool.allocateNode(childCx, childCy, half)
            pool.setChild(node, g, created)
            return created
        }
        return existing
    }

    private fun accumulate(node: Int, x: Double, y: Double, m: Double, r: Double) {
        pool.mass[node] += m
        pool.cmX[node] += m * x
        pool.cmY[node] += m * y
        pool.radSum[node] += r
        pool.count[node]++
    }

    private fun insertBody(
        i: Int,
        x: Double,
        y: Double,
        mi: Double,
        ri: Double,
        node: Int,
        depth: Int,
    ) {
        accumulate(node, x, y, mi, ri)

        if (pool.isLeaf(node)) {
            val existing = pool.bodyIndex[node]
            when {
                existing == QuadtreePool.EMPTY -> pool.bodyIndex[node] = i
                existing == QuadtreePool.BUCKET -> return
                depth >= BARNES_HUT_MAX_DEPTH -> pool.bodyIndex[node] = QuadtreePool.BUCKET
                else -> splitLeaf(node, existing, i, x, y, mi, ri, depth)
            }
            return
        }

        val child = childFor(node, x, y)
        insertBody(i, x, y, mi, ri, child, depth + 1)
    }

    private fun splitLeaf(
        node: Int,
        existing: Int,
        incoming: Int,
        ix: Double,
        iy: Double,
        im: Double,
        ir: Double,
        depth: Int,
    ) {
        pool.bodyIndex[node] = QuadtreePool.EMPTY
        val ex = pX[existing]
        val ey = pY[existing]
        val em = pMass[existing]
        val er = pRadius[existing]

        val childE = childFor(node, ex, ey)
        insertBody(existing, ex, ey, em, er, childE, depth + 1)

        val childI = childFor(node, ix, iy)
        insertBody(incoming, ix, iy, im, ir, childI, depth + 1)
    }
}

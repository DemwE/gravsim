package engine

import kotlin.math.sqrt

/**
 * Fixed-root 2D Barnes–Hut quadtree algorithm.
 *
 * INVARIANT: [build] must be called before [computeRange]; the body indices stored inside
 * leaves refer to array positions at build time, so the physics arrays must not be reordered
 * between [build] and [computeRange].
 */
class BarnesHut internal constructor(
    private val pool: QuadtreePool = QuadtreePool()
) {
    private var pX: DoubleArray = DoubleArray(0)
    private var pY: DoubleArray = DoubleArray(0)
    private var pMass: DoubleArray = DoubleArray(0)
    private var pRadius: DoubleArray = DoubleArray(0)

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
        for (k in start until end) {
            ax[k] = 0.0
            ay[k] = 0.0
        }
        for (i in start until end) {
            traverseForce(i, px[i], py[i], masses[i], radii[i], 0, true, theta, ax, ay)
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

    private fun traverseForce(
        i: Int,
        ix: Double,
        iy: Double,
        im: Double,
        ir: Double,
        node: Int,
        containsSelf: Boolean,
        theta: Double,
        ax: DoubleArray,
        ay: DoubleArray,
    ) {
        if (pool.count[node] == 0) return
        if (pool.bodyIndex[node] == i) return

        if (pool.isLeaf(node)) {
            applyLeafForce(i, ix, iy, im, ir, node, containsSelf, ax, ay)
            return
        }

        val cx0 = pool.cx[node]
        val cy0 = pool.cy[node]
        val sub = pool.radSum[node] / pool.count[node]

        var collapsed = false
        computeEffectiveTarget(node, ix, iy, im, containsSelf) { effMass, cxm, cym ->
            if (shouldCollapse(pool.h[node], ix, iy, cxm, cym, theta)) {
                applyForce(i, ix, iy, ax, ay, effMass, cxm, cym, ir + sub)
                collapsed = true
            }
        }
        if (collapsed) return

        val gi = quadrant(ix, iy, cx0, cy0)
        for (c in 0 until 4) {
            val child = pool.child(node, c)
            if (child != QuadtreePool.EMPTY) {
                traverseForce(i, ix, iy, im, ir, child, containsSelf && c == gi, theta, ax, ay)
            }
        }
    }

    private fun applyLeafForce(
        i: Int,
        ix: Double,
        iy: Double,
        im: Double,
        ir: Double,
        node: Int,
        containsSelf: Boolean,
        ax: DoubleArray,
        ay: DoubleArray,
    ) {
        val sub = pool.radSum[node] / pool.count[node]
        computeEffectiveTarget(node, ix, iy, im, containsSelf && pool.bodyIndex[node] == QuadtreePool.BUCKET) { effMass, cxm, cym ->
            applyForce(i, ix, iy, ax, ay, effMass, cxm, cym, ir + sub)
        }
    }

    private inline fun computeEffectiveTarget(
        node: Int,
        ix: Double,
        iy: Double,
        im: Double,
        containsSelf: Boolean,
        action: (targetMass: Double, cxm: Double, cym: Double) -> Unit,
    ) {
        if (containsSelf) {
            val eff = pool.mass[node] - im
            if (eff > 0.0) {
                action(eff, (pool.cmX[node] - im * ix) / eff, (pool.cmY[node] - im * iy) / eff)
            }
        } else {
            val mm = pool.mass[node]
            action(mm, pool.cmX[node] / mm, pool.cmY[node] / mm)
        }
    }

    private fun shouldCollapse(
        h0: Double,
        ix: Double,
        iy: Double,
        cxm: Double,
        cym: Double,
        theta: Double,
    ): Boolean {
        val dx = cxm - ix
        val dy = cym - iy
        val d = sqrt(dx * dx + dy * dy)
        return (2.0 * h0) < theta * d
    }

    private fun applyForce(
        i: Int,
        ix: Double,
        iy: Double,
        ax: DoubleArray,
        ay: DoubleArray,
        targetMass: Double,
        cxm: Double,
        cym: Double,
        soft: Double,
    ) {
        val dx = cxm - ix
        val dy = cym - iy
        val distSq = dx * dx + dy * dy + soft * soft
        val inv = 1.0 / sqrt(distSq)
        val f = G * targetMass * inv * inv * inv
        ax[i] += dx * f
        ay[i] += dy * f
    }
}

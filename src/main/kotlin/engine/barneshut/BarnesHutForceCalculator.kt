package engine.barneshut

import engine.G
import engine.QuadtreePool
import kotlin.math.sqrt

internal class BarnesHutForceCalculator(
    private val pool: QuadtreePool
) {

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
        val isBucket = pool.bodyIndex[node] == QuadtreePool.BUCKET
        computeEffectiveTarget(node, ix, iy, im, containsSelf && isBucket) { effMass, cxm, cym ->
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

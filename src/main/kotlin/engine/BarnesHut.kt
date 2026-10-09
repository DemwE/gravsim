package engine

import kotlin.math.sqrt

/**
 * Fixed-root 2D Barnes–Hut quadtree with a pooled flat-node layout.
 *
 * INVARIANT: [build] must be called before [computeRange]; the body indices stored inside
 * leaves refer to array positions at build time, so the physics arrays must not be reordered
 * between [build] and [computeRange]. Successive [build] calls fully reset the pool, so
 * nothing persists across steps.
 *
 * The tree root is the fixed box `[-MAP_HALF_KM, MAP_HALF_KM]^2` centered on the barycenter,
 * which bounds subdivision depth for any distribution.
 */
internal class BarnesHut {

    private companion object {
        const val EMPTY = -1
        const val BUCKET = -3
    }

    private var mass = DoubleArray(0)
    private var cmX = DoubleArray(0)
    private var cmY = DoubleArray(0)
    private var radSum = DoubleArray(0)
    private var count = IntArray(0)
    private var cx = DoubleArray(0)
    private var cy = DoubleArray(0)
    private var h = DoubleArray(0)
    private var children = IntArray(0)
    private var bodyIndex = IntArray(0)

    private var nodesCreated = 0

    private var pX: DoubleArray = DoubleArray(0)
    private var pY: DoubleArray = DoubleArray(0)
    private var pMass: DoubleArray = DoubleArray(0)
    private var pRadius: DoubleArray = DoubleArray(0)

    fun build(px: DoubleArray, py: DoubleArray, masses: DoubleArray, radii: DoubleArray, n: Int) {
        if (n == 0) return
        ensureNodeCapacity(n)
        resetNodes()
        pX = px
        pY = py
        pMass = masses
        pRadius = radii
        allocateNode(0.0, 0.0, MAP_HALF_KM)
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

    private fun ensureNodeCapacity(n: Int) {
        val target = maxOf(4 * n + 16, mass.size)
        if (target <= mass.size) return
        val newSize = maxOf(target, mass.size * 2)

        mass = mass.copyOf(newSize)
        cmX = cmX.copyOf(newSize)
        cmY = cmY.copyOf(newSize)
        radSum = radSum.copyOf(newSize)
        count = count.copyOf(newSize)
        cx = cx.copyOf(newSize)
        cy = cy.copyOf(newSize)
        h = h.copyOf(newSize)
        children = children.copyOf(newSize * 4)
        bodyIndex = bodyIndex.copyOf(newSize)
    }

    private fun growNodeCapacity() {
        val newSize = maxOf(16, mass.size * 2)
        mass = mass.copyOf(newSize)
        cmX = cmX.copyOf(newSize)
        cmY = cmY.copyOf(newSize)
        radSum = radSum.copyOf(newSize)
        count = count.copyOf(newSize)
        cx = cx.copyOf(newSize)
        cy = cy.copyOf(newSize)
        h = h.copyOf(newSize)
        children = children.copyOf(newSize * 4)
        bodyIndex = bodyIndex.copyOf(newSize)
    }

    private fun resetNodes() {
        for (i in 0 until nodesCreated) {
            mass[i] = 0.0
            cmX[i] = 0.0
            cmY[i] = 0.0
            radSum[i] = 0.0
            count[i] = 0
            bodyIndex[i] = EMPTY
            val base = i shl 2
            children[base] = EMPTY
            children[base + 1] = EMPTY
            children[base + 2] = EMPTY
            children[base + 3] = EMPTY
        }
        nodesCreated = 0
    }

    private fun allocateNode(cx0: Double, cy0: Double, h0: Double): Int {
        if (nodesCreated >= mass.size) {
            growNodeCapacity()
        }
        val idx = nodesCreated
        nodesCreated++
        cx[idx] = cx0
        cy[idx] = cy0
        h[idx] = h0
        mass[idx] = 0.0
        cmX[idx] = 0.0
        cmY[idx] = 0.0
        radSum[idx] = 0.0
        count[idx] = 0
        bodyIndex[idx] = EMPTY
        val base = idx shl 2
        children[base] = EMPTY
        children[base + 1] = EMPTY
        children[base + 2] = EMPTY
        children[base + 3] = EMPTY
        return idx
    }

    private fun isLeaf(node: Int): Boolean {
        val base = node shl 2
        return children[base] == EMPTY &&
            children[base + 1] == EMPTY &&
            children[base + 2] == EMPTY &&
            children[base + 3] == EMPTY
    }

    private fun quadrant(x: Double, y: Double, cx0: Double, cy0: Double): Int =
        (if (x >= cx0) 2 else 0) or (if (y >= cy0) 1 else 0)

    private fun childFor(node: Int, x: Double, y: Double): Int {
        val g = quadrant(x, y, cx[node], cy[node])
        val idx = (node shl 2) or g
        if (children[idx] == EMPTY) {
            val half = h[node] * 0.5
            val childCx = cx[node] + if ((g and 2) != 0) half else -half
            val childCy = cy[node] + if ((g and 1) != 0) half else -half
            children[idx] = allocateNode(childCx, childCy, half)
        }
        return children[idx]
    }

    private fun accumulate(node: Int, x: Double, y: Double, m: Double, r: Double) {
        mass[node] += m
        cmX[node] += m * x
        cmY[node] += m * y
        radSum[node] += r
        count[node]++
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

        if (isLeaf(node)) {
            val existing = bodyIndex[node]
            when {
                existing == EMPTY -> bodyIndex[node] = i
                existing == BUCKET -> return
                depth >= BARNES_HUT_MAX_DEPTH -> bodyIndex[node] = BUCKET
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
        bodyIndex[node] = EMPTY
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
        if (count[node] == 0) return
        if (bodyIndex[node] == i) return

        if (isLeaf(node)) {
            if (bodyIndex[node] == BUCKET) {
                applyAggregate(i, ix, iy, im, ir, node, containsSelf, ax, ay)
            } else {
                applyExactLeaf(i, ix, iy, ir, node, ax, ay)
            }
            return
        }

        val cx0 = cx[node]
        val cy0 = cy[node]
        val sub = radSum[node] / count[node]

        if (containsSelf) {
            val eff = mass[node] - im
            if (eff <= 0.0) return
            val cxm = (cmX[node] - im * ix) / eff
            val cym = (cmY[node] - im * iy) / eff
            if (shouldCollapse(cx0, cy0, h[node], ix, iy, cxm, cym, theta)) {
                applyForce(i, ix, iy, ax, ay, eff, cxm, cym, ir + sub)
                return
            }
        } else {
            val mm = mass[node]
            val cxm = cmX[node] / mm
            val cym = cmY[node] / mm
            if (shouldCollapse(cx0, cy0, h[node], ix, iy, cxm, cym, theta)) {
                applyForce(i, ix, iy, ax, ay, mm, cxm, cym, ir + sub)
                return
            }
        }

        val base = node shl 2
        val gi = quadrant(ix, iy, cx0, cy0)
        for (c in 0 until 4) {
            val child = children[base + c]
            if (child != EMPTY) {
                traverseForce(i, ix, iy, im, ir, child, containsSelf && c == gi, theta, ax, ay)
            }
        }
    }

    private fun shouldCollapse(
        cx0: Double,
        cy0: Double,
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

    private fun applyExactLeaf(
        i: Int,
        ix: Double,
        iy: Double,
        ir: Double,
        node: Int,
        ax: DoubleArray,
        ay: DoubleArray,
    ) {
        val mm = mass[node]
        val cxm = cmX[node] / mm
        val cym = cmY[node] / mm
        applyForce(i, ix, iy, ax, ay, mm, cxm, cym, ir + radSum[node] / count[node])
    }

    private fun applyAggregate(
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
        val sub = radSum[node] / count[node]
        if (containsSelf) {
            val eff = mass[node] - im
            if (eff <= 0.0) return
            val cxm = (cmX[node] - im * ix) / eff
            val cym = (cmY[node] - im * iy) / eff
            applyForce(i, ix, iy, ax, ay, eff, cxm, cym, ir + sub)
        } else {
            val mm = mass[node]
            val cxm = cmX[node] / mm
            val cym = cmY[node] / mm
            applyForce(i, ix, iy, ax, ay, mm, cxm, cym, ir + sub)
        }
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

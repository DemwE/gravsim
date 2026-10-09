package engine

internal class QuadtreePool {

    companion object {
        const val EMPTY = -1
        const val BUCKET = -3
    }

    var mass = DoubleArray(0)
    var cmX = DoubleArray(0)
    var cmY = DoubleArray(0)
    var radSum = DoubleArray(0)
    var count = IntArray(0)
    var cx = DoubleArray(0)
    var cy = DoubleArray(0)
    var h = DoubleArray(0)
    var children = IntArray(0)
    var bodyIndex = IntArray(0)

    var nodesCreated = 0
        private set

    fun ensureCapacity(n: Int) {
        val target = maxOf(4 * n + 16, mass.size)
        if (target > mass.size) {
            growTo(maxOf(target, mass.size * 2))
        }
    }

    fun growNodeCapacity() {
        growTo(maxOf(16, mass.size * 2))
    }

    private fun growTo(newSize: Int) {
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

    fun reset() {
        for (i in 0 until nodesCreated) {
            initNode(i, 0.0, 0.0, 0.0)
        }
        nodesCreated = 0
    }

    fun allocateNode(cx0: Double, cy0: Double, h0: Double): Int {
        if (nodesCreated >= mass.size) {
            growNodeCapacity()
        }
        val idx = nodesCreated++
        initNode(idx, cx0, cy0, h0)
        return idx
    }

    private fun initNode(idx: Int, cx0: Double, cy0: Double, h0: Double) {
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
    }

    fun isLeaf(node: Int): Boolean {
        val base = node shl 2
        return children[base] == EMPTY &&
            children[base + 1] == EMPTY &&
            children[base + 2] == EMPTY &&
            children[base + 3] == EMPTY
    }

    fun child(node: Int, quad: Int): Int = children[(node shl 2) or quad]

    fun setChild(node: Int, quad: Int, childIdx: Int) {
        children[(node shl 2) or quad] = childIdx
    }
}

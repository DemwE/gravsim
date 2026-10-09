import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import engine.BarnesHut
import engine.G
import kotlin.math.sqrt
import kotlin.random.Random

@Tag("benchmark")
class BarnesHutBenchmarkTest {

    private val sizes = intArrayOf(100, 300, 1000, 3000, 10_000)
    private val theta = 0.5

    private val barnesHut = BarnesHut()
    private var px = DoubleArray(0)
    private var py = DoubleArray(0)
    private var masses = DoubleArray(0)
    private var radii = DoubleArray(0)
    private var ax = DoubleArray(0)
    private var ay = DoubleArray(0)

    @Test
    fun report() {
        println()
        println("N-body gravity benchmark (km, s, kg)")
        println("theta(BH) = 0.5, fixed 50 AU map, warmup + min-of-3 samples")
        println()
        println(
            "%10s | %-14s | %-14s | %-14s | %-14s".format(
                "N", "BH ms/step", "BH steps/s", "Naive ms/step", "Speedup"
            )
        )
        println("-".repeat(74))

        for (n in sizes) {
            val random = Random(n.toLong())
            px = DoubleArray(n)
            py = DoubleArray(n)
            masses = DoubleArray(n)
            radii = DoubleArray(n)
            ax = DoubleArray(n)
            ay = DoubleArray(n)

            for (i in 0 until n) {
                px[i] = random.nextDouble(-2.0e8, 2.0e8)
                py[i] = random.nextDouble(-2.0e8, 2.0e8)
                masses[i] = if (i < maxOf(1, n / 50)) {
                    random.nextDouble(1.0e29, 1.0e31)
                } else {
                    random.nextDouble(1.0e24, 1.0e27)
                }
                radii[i] = random.nextDouble(5.0e3, 5.0e5)
            }

            val bhMs = measureMinMs(warmup = 20) { bhStep(n) }
            val bhStepsPerSec = 1000.0 / bhMs

            val naiveMs = if (n <= 3000) {
                measureMinMs(warmup = 5) { naiveStep(n) }
            } else {
                Double.NaN
            }
            val speedup = if (naiveMs.isNaN()) Double.NaN else naiveMs / bhMs

            val naiveCell = if (naiveMs.isNaN()) "-" else "%.3f".format(naiveMs)
            val speedupCell = if (speedup.isNaN()) "-" else "%.2fx".format(speedup)

            println(
                "%10d | %-14.3f | %-14.1f | %-14s | %-14s".format(
                    n, bhMs, bhStepsPerSec, naiveCell, speedupCell
                )
            )
        }
        println()

        assertTrue(true, "Benchmark completed")
    }

    private fun bhStep(n: Int) {
        barnesHut.build(px, py, masses, radii, n)
        barnesHut.computeRange(px, py, masses, radii, ax, ay, 0, n, theta)
        for (k in 0 until n) {
            px[k] += ax[k]
            py[k] += ay[k]
        }
    }

    private fun naiveStep(n: Int) {
        val ax = DoubleArray(n)
        val ay = DoubleArray(n)

        for (i in 0 until n) {
            val xi = px[i]
            val yi = py[i]
            val ri = radii[i]
            val mi = masses[i]

            for (j in i + 1 until n) {
                val dx = px[j] - xi
                val dy = py[j] - yi

                val softening = ri + radii[j]
                val distSq = dx * dx + dy * dy + softening * softening
                val invDist = 1.0 / sqrt(distSq)
                val invDist3 = invDist * invDist * invDist

                val force = G * invDist3
                val fA = force * masses[j]
                val fB = force * mi

                ax[i] += dx * fA
                ay[i] += dy * fA
                ax[j] -= dx * fB
                ay[j] -= dy * fB
            }
        }

        for (k in 0 until n) {
            px[k] += ax[k]
            py[k] += ay[k]
        }
    }

    private fun measureMinMs(warmup: Int, block: () -> Unit): Double {
        var best = Double.MAX_VALUE
        repeat(3) {
            repeat(warmup) { block() }
            val start = System.nanoTime()
            var steps = 0
            while (System.nanoTime() - start < 400_000_000L) {
                block()
                steps++
            }
            val elapsed = (System.nanoTime() - start) / 1e6
            best = minOf(best, elapsed / steps)
        }
        return best
    }
}

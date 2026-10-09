package engine

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class ParallelDispatcher(threads: Int) {
    val threadCount: Int = threads.coerceAtLeast(1)
    private var pool: ExecutorService? = null

    fun executeParallel(count: Int, task: (start: Int, end: Int) -> Unit) {
        val usedThreads = minOf(threadCount, count)
        val block = (count + usedThreads - 1) / usedThreads

        val tasks = ArrayList<Callable<Unit>>(usedThreads - 1)
        for (t in 0 until usedThreads - 1) {
            val start = t * block
            val end = minOf(start + block, count)
            tasks += Callable { task(start, end) }
        }

        val inlineStart = minOf((usedThreads - 1) * block, count)
        task(inlineStart, count)

        getPool().invokeAll(tasks)
    }

    private fun getPool(): ExecutorService {
        pool?.let { return it }
        val created = Executors.newFixedThreadPool(threadCount) { runnable ->
            Thread(runnable, "gravsim-physics").apply { isDaemon = true }
        }
        pool = created
        return created
    }

    fun close() {
        pool?.shutdownNow()
        pool = null
    }
}

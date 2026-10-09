package lib

import java.security.SecureRandom
import java.util.Base64

data class Seed(val value: Long) {
    constructor(rawSeed: Any?) : this(parseSeed(rawSeed))

    companion object {
        private fun parseSeed(seed: Any?): Long {
            return when (seed) {
                is Long -> seed
                is Int -> seed.toLong()
                is String -> {
                    try {
                        val bytes = Base64.getDecoder().decode(seed)
                        val random = SecureRandom.getInstance("SHA1PRNG")
                        random.setSeed(bytes)
                        random.nextLong()
                    } catch (_: IllegalArgumentException) {
                        seed.hashCode().toLong()
                    }
                }
                null -> SecureRandom().nextLong()
                else -> seed.hashCode().toLong()
            }
        }
    }
}
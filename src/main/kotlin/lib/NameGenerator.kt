package lib

import kotlin.random.Random

object NameGenerator {

    private val SECTOR_NAMES = listOf(
        "Synuefe", "Col 285 Sector", "Pleiades Sector", "Wregoe", "Outotz", "Vela Dark Region",
        "California Sector", "Swoiwns", "Prua Phoe", "Eol Prou", "Dryooe Prou", "Gludgoea",
        "Hypoae Free", "NGC 7822 Sector", "Traikaae", "Blaa Hypue", "Bleae Thua", "Thraikoo",
        "Nuakeea", "Byoomi", "Stuelae", "Flyiedgai", "Phylur", "Oephaidy", "Juenae", "Smojeae",
        "Eoch Flyua", "Dryio Bluae", "Greeroi", "Agnaiz", "Hyuqu", "Oevasy", "Vera", "LBN 623",
        "NGC 6357 Sector", "IC 4604 Sector", "Running Man Sector", "Orion Nebula", "Coalsack Sector"
    )

    private val CATALOG_PREFIXES = listOf(
        "HIP", "HD", "HR", "LHS", "Gliese", "Wolf", "Ross", "LTT", "BD", "CD"
    )

    private val NAMED_SYSTEMS = listOf(
        "Sol", "Achenar", "Sirius", "Procyon", "Barnard's Star", "Betelgeuse", "Rigel",
        "Vega", "Altair", "Canopus", "Arcturus", "Capella", "Aldebaran", "Spica", "Antares",
        "Pollux", "Fomalhaut", "Deneb", "Regulus", "Maia", "Merope", "Electra", "Taygeta",
        "Celaeno", "Asterope", "Alcyone", "Atlas", "Pleione", "Alioth", "Epsilon Indi",
        "Tau Ceti", "Lave", "Diso", "Riedquat", "Zaonce", "Tionisla", "Leesti", "Eravate",
        "Kremainn", "LHS 3447", "Asellus Primus", "Dromi", "Sharur", "Matet", "Beagle Point"
    )

    private val MASS_CODES = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')

    fun generateSystemName(random: Random): String {
        return when (random.nextInt(100)) {
            in 0..9 -> {
                NAMED_SYSTEMS.random(random)
            }
            in 10..34 -> {
                val prefix = CATALOG_PREFIXES.random(random)
                val number = when (prefix) {
                    "HIP" -> random.nextInt(100, 120000)
                    "HD" -> random.nextInt(1000, 250000)
                    "HR" -> random.nextInt(1, 9110)
                    "LHS" -> random.nextInt(100, 4000)
                    "Gliese" -> random.nextInt(1, 999)
                    "Wolf" -> random.nextInt(1, 1500)
                    "Ross" -> random.nextInt(1, 999)
                    "LTT" -> random.nextInt(1000, 9999)
                    "BD" -> "+${random.nextInt(10, 80)} ${random.nextInt(100, 9999)}"
                    "CD" -> "-${random.nextInt(10, 80)} ${random.nextInt(100, 9999)}"
                    else -> random.nextInt(100, 99999)
                }
                "$prefix $number"
            }
            else -> {
                val sector = SECTOR_NAMES.random(random)
                val l1 = ('A'..'Z').random(random)
                val l2 = ('A'..'Z').random(random)
                val l3 = ('A'..'Z').random(random)
                val massCode = MASS_CODES.random(random)
                val index1 = random.nextInt(0, 30)
                val index2 = random.nextInt(0, 150)
                "$sector $l1$l2-$l3 $massCode$index1-$index2"
            }
        }
    }

    fun formatStarName(systemName: String, starIndex: Int, totalStars: Int): String {
        return if (totalStars <= 1) {
            "$systemName A"
        } else {
            val letter = ('A' + starIndex)
            "$systemName $letter"
        }
    }

    fun formatPlanetName(systemName: String, planetIndex: Int, totalStars: Int): String {
        val number = planetIndex + 1
        return if (totalStars <= 1) {
            "$systemName $number"
        } else {
            "$systemName AB $number"
        }
    }
}

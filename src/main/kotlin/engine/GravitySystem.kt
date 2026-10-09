package engine

const val G: Double = 6.67430e-20
const val MAX_PHYSICS_STEP: Double = 600.0
const val MAX_SUBSTEPS: Int = 32

const val AU_KM: Double = 1.496e8
const val MAP_HALF_AU: Double = 50.0
const val MAP_HALF_KM: Double = MAP_HALF_AU * AU_KM

const val BARNES_HUT_THETA: Double = 0.5
const val BARNES_HUT_PARALLEL_MIN_BODIES: Int = 64
const val BARNES_HUT_MAX_DEPTH: Int = 64

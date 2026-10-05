package spaceObjects

import bodyTypes.StarType
import units.Kelvin
import units.Kilogram
import units.Quantity
import units.SolarMass

data class Star(
    val name: String,
    val type: StarType,
    val mass: Quantity<SolarMass>,
    val radius: Quantity<Kilogram>,
    val temperature: Quantity<Kelvin>
)

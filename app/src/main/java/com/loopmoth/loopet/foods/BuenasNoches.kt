package com.loopmoth.loopet.foods

import com.loopmoth.loopet.interfaces.Food

class BuenasNoches: Food {
    override val name: String = "Buenas Noches"
    override val ratio: Int = 10
    override val is_healing: Boolean = false
    override val healing_ratio: Int = 0
}
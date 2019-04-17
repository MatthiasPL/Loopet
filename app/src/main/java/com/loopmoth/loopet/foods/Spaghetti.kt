package com.loopmoth.loopet.foods

import com.loopmoth.loopet.interfaces.Food

class Spaghetti: Food {
    override val name: String = "Spaghetti"
    override val ratio: Int = 60
    override val is_healing: Boolean = false
    override val healing_ratio: Int = 0
}
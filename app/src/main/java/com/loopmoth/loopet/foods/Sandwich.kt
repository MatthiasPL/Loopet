package com.loopmoth.loopet.foods

import com.loopmoth.loopet.interfaces.Food

class Sandwich: Food {
    override val name: String = "Cheese Sandwich"
    override val ratio: Int = 30
    override val is_healing: Boolean = false
    override val healing_ratio: Int = 0
}
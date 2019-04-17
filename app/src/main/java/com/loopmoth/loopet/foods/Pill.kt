package com.loopmoth.loopet.foods

import com.loopmoth.loopet.interfaces.Food

class Pill: Food {
    override val name: String = "Pill"
    override val ratio: Int = 10
    override val healing_ratio: Int = 75
    override val is_healing: Boolean = true
}
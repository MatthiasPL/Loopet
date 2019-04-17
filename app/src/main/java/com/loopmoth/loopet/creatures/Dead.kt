package com.loopmoth.loopet.creatures

import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature

class Dead: Creature {
    override var name: String = "Dead"
    override var stadium: Stadium = Stadium.DEAD

    override var max_happiness: Int = 0
    override var max_health: Int = 0
    override var max_hunger: Int = 0
    override var max_poop: Int = 0
}
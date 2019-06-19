package com.loopmoth.loopet.creatures.Adult

import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature

class Looying: Creature {
    override var name: String = "Looying"
    override var stadium: Stadium = Stadium.ADULT

    override var max_happiness: Int = 100
    override var max_health: Int = 100
    override var max_hunger: Int = 100
    override var max_poop: Int = 100
}
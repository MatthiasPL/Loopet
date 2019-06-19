package com.loopmoth.loopet.creatures.Old

import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature

class Loolong: Creature {
    override var name: String = "Loolong"
    override var stadium: Stadium = Stadium.OLD

    override var max_happiness: Int = 100
    override var max_health: Int = 100
    override var max_hunger: Int = 100
    override var max_poop: Int = 100
}
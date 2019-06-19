package com.loopmoth.loopet.creatures.Young

import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature

class Loohan: Creature {
    override var name: String = "Loohan"
    override var stadium: Stadium = Stadium.YOUNG

    override var max_happiness: Int = 100
    override var max_health: Int = 100
    override var max_hunger: Int = 100
    override var max_poop: Int = 100
}
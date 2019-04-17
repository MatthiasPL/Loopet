package com.loopmoth.loopet.creatures.Baby

import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature

class Loopel: Creature {
    override var name: String = "Loopel"
    override var stadium: Stadium = Stadium.BABY

    override var max_happiness: Int = 100
    override var max_health: Int = 100
    override var max_hunger: Int = 100
    override var max_poop: Int = 100
}
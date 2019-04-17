package com.loopmoth.loopet.interfaces

import com.loopmoth.loopet.enums.Stadium

interface Creature {
    var name: String
    var stadium: Stadium

    var max_hunger: Int
    var max_happiness: Int
    var max_poop: Int
    var max_health: Int

    fun Feed(food: Food){

    }
    fun Wash(){

    }
    fun Rise(creature: Creature){

    }
    fun Kill(){

    }
    fun PlayWith(){

    }
    fun Fail(){

    }
    fun ChangeLight(){

    }
    fun Sleep(){

    }
    fun GetOlder(deltaAge: Double){

    }

}
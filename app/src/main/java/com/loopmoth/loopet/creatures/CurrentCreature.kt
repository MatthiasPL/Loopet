package com.loopmoth.loopet.creatures

import android.widget.Toast
import com.loopmoth.loopet.enums.Stadium
import com.loopmoth.loopet.interfaces.Creature
import com.loopmoth.loopet.interfaces.Food

class CurrentCreature: Creature {

    constructor(creature: Creature){
        name = creature.name
        stadium = creature.stadium
        max_happiness = creature.max_happiness
        max_health = creature.max_health
        max_hunger = creature.max_hunger
        max_poop = creature.max_poop
    }

    override var name: String
    override var stadium: Stadium
    override var max_happiness: Int
    override var max_health: Int
    override var max_hunger: Int
    override var max_poop: Int

    var age: Double = 0.0
    var hunger: Int = 100
    var happiness: Int = 100
    var poop: Int = 100
    var weight: Int = 1
    var health: Int = 100

    var is_dark: Boolean = false
    var is_ready_to_rise: Boolean = false
    var is_sleeping: Boolean = false

    var is_ill: Boolean = false
    var is_hungry: Boolean = false
    var is_sleepy: Boolean = false
    var is_poop: Boolean = false
    var is_sad: Boolean = false
    var is_dead: Boolean = false

    private var care_mistakes: Int = 0

    override fun ChangeLight() {
        is_dark = !is_dark
    }

    override fun Sleep() {
        if(!is_sleeping && is_dark){
            is_sleeping
        }
    }

    override fun Feed(food: Food) {
        val tempHunger = hunger + food.ratio

        if(tempHunger > max_hunger){
            //Nie udało się
            //Toast.makeText(this, "Jest pełny", Toast.LENGTH_SHORT).show()
        }
        else{
            hunger = tempHunger

            //pomyślnie nakarmiono
        }
    }

    override fun Wash() {
        if(is_poop){
            is_poop = false
            poop = max_poop
            //pomyślnie umyto
        }
        else{
            //nie ma nic do czyszczenia
        }
    }

    override fun Fail() {
        care_mistakes++
    }

    override fun PlayWith() {
        if(happiness < max_happiness/2){
            happiness = max_happiness
            //stworek szczęśliwy
        }
        else{
            //nie chce się bawić
        }
    }

    override fun Kill() {
        //umarł
        val dead_creature: Creature = Dead()
        stadium = dead_creature.stadium
        max_happiness = dead_creature.max_happiness
        max_health = dead_creature.max_health
        max_hunger = dead_creature.max_hunger
        max_poop = dead_creature.max_poop
    }

    override fun Rise(creature: Creature) {
        //ewoluuje
        //TODO: drzewko ewolucji, pewnie w gameengine

        stadium = creature.stadium
        max_happiness = creature.max_happiness
        max_health = creature.max_health
        max_hunger = creature.max_hunger
        max_poop = creature.max_poop
    }

    override fun GetOlder(deltaAge: Double) {
        age+=deltaAge
    }
    
}
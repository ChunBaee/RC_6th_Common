package com.jcorp.rc_mission_6

import android.graphics.drawable.Drawable

object Dices {
    val Fire : MutableList<Int> = mutableListOf(0,R.drawable.fire_1, R.drawable.fire_2, R.drawable.fire_3, R.drawable.fire_4, R.drawable.fire_5, R.drawable.fire_6, R.drawable.fire_7)
    val Ice : MutableList<Int> = mutableListOf(0,R.drawable.ice_1, R.drawable.ice_2, R.drawable.ice_3, R.drawable.ice_4, R.drawable.ice_5, R.drawable.ice_6, R.drawable.ice_7)
    val Electric : MutableList<Int> = mutableListOf(0,R.drawable.elec_1, R.drawable.elec_2, R.drawable.elec_3, R.drawable.elec_4, R.drawable.elec_5, R.drawable.elec_6, R.drawable.elec_7)
    val Heal : MutableList<Int> = mutableListOf(0,R.drawable.heal_1, R.drawable.heal_2, R.drawable.heal_3, R.drawable.heal_4, R.drawable.heal_5, R.drawable.heal_6, R.drawable.heal_7)

    val DiceType : MutableList<String> = mutableListOf("Fire", "Ice", "Electric", "Heal")
}
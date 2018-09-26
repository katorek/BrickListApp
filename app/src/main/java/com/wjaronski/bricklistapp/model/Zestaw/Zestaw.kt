package com.wjaronski.bricklistapp.model.Zestaw
import com.wjaronski.bricklistapp.model.Klocek.Klocek
import java.util.*


/**
 * Created by wojta on 28.05.2018.
 */
class Zestaw(
        var id: Int,
        var Name: String,
        val listaKlockow: LinkedList<Klocek>,
        var Active: Int,
        var LastAccessed: Int
) {

    companion object {
        val TABLE_NAME = "Inventories"
        val ID = "id"
        val NAME = "Name"
        val ACTIVE = "Active"
        val LAST_ACCESSED = "LastAccessed"
    }

    fun getOpis(): CharSequence? {
        return Name+ if (Active==0) " archived" else ""
    }
    fun getDescription(): CharSequence? {
//        var lKlockow = 0
//        listaKlockow.forEach {
//            lKlockow += (it.qty -it.qtyInStore)
//        }

//        return "Liczba brakujacych klockow $lKlockow"
        return "[$id] Ostatnio uzywany $LastAccessed, status: "+ (if(Active == 1) "aktywny" else "nie aktywny")
    }

    constructor(): this(0,"",LinkedList<Klocek>(),1,0)

}


package com.enginemachiner.harmony

/*
 TODO: Check how to change the nbt display properly (stack custom name).
 TODO: Register NBT client to server networking here. Remember, slot, blockPos if block, uuid if non player entity.
 TODO: Should there be a close screen registered receiver or screen updater?
*/

const val TICKS_PER_SECOND = 20

open class Mod( val name: String ) {

    val id = name.lowercase()

    init {

        val isBlank = name.isBlank();           if ( isBlank ) throw IllegalStateException(BLANK)

    }

    /** @see Chat */
    val chat = Chat(this)

    /**@see Debug */
    val debug = Debug(this)

    /** @see File */
    open val file = File(this)

    /** @see Identifiers */
    open val identifiers = Identifiers(this)

    /** @see ModAdvancement */
    open val advancement = ModAdvancement(this)

    /** @see ServerNetworking */
    val networkingManager = ServerNetworking(this)

    open val itemGroup = itemGroup()
    open val translation = Translation(name)

    private companion object {

        const val BLANK = "Mod name can't be blank!"

    }

}
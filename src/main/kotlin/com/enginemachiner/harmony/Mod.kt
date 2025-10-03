package com.enginemachiner.harmony

/*
 TODO: Check how to change the nbt display properly (stack custom name).
 TODO: Register NBT client to server networking here. Remember, slot, blockPos if block, uuid if non player entity.
 TODO: Should there be a close screen registered receiver or screen updater?
 TODO: Document what needs to be documented.
*/

const val TICKS_PER_SECOND = 20

open class Mod( val name: String ) {

    val id = name.lowercase()

    init {

        val isBlank = name.isBlank()

        if ( isBlank ) throw IllegalStateException(BLANK)

    }

    /** Utility for handling informative in-game chat messages of the mod and its formatting. */
    val chat = Chat(this)

    /** Debugging utility for logging and development purposes.
     * Provides methods for printing debug information to the console.
     */
    val debug = Debug(this)

    open val identifiers = Identifiers(this)

    open val file = File(this);          open val itemGroup = itemGroup()

    open val translation = Translation(name)

    open val advancement = ModAdvancement(this)

    val networkingManager = ServerNetworking(this)

    private companion object {

        const val BLANK = "Mod name can't be blank!"

    }

}
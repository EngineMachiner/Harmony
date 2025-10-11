package com.enginemachiner.harmony

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

const val TICKS_PER_SECOND = 20

open class Mod( val name: String ) {

    val id = name.lowercase()

    init {

        val isBlank = name.isBlank();           if ( isBlank ) throw IllegalStateException(BLANK)

    }

    /**@see Debug */
    open val debug = Debug(this)

    /**@see Translation */
    open val translation = Translation(name)

    fun nbt( nbt: NbtCompound ) = nbt.getCompound(name)!!

    fun nbt( itemStack: ItemStack ) = nbt( itemStack.nbt!! )

    private companion object {

        const val BLANK = "Mod name can't be blank!"

    }

}
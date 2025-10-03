package com.enginemachiner.harmony

import net.minecraft.text.MutableText
import net.minecraft.text.Text.translatable

class Translation( private val namespace: String ) {

    val advancement = Advancement()

    fun get( key: String ): MutableText = translatable("$namespace.$key")
    fun item( key: String ): MutableText = translatable("item.$namespace.$key")
    fun block( key: String ): MutableText = translatable("block.$namespace.$key")
    fun entity( key: String ): MutableText = translatable("entity.$namespace.$key")
    fun enchantment( key: String ): MutableText = translatable("enchantment.$namespace.$key")

    inner class Advancement {

        fun title( key: String ): MutableText = get("advancements.$key.title")
        fun description( key: String ): MutableText = get("advancements.$key.description")

    }

}
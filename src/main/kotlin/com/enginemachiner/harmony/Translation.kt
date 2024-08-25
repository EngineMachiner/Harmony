package com.enginemachiner.harmony

import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableText

object Translation {

    fun has( key: String ): Boolean { return get(key) != "$MOD_NAME.$key" }
    fun get( key: String ): String { return TranslatableText( "$MOD_NAME.$key" ).string }
    fun item( key: String ): String { return TranslatableText( "item.$MOD_NAME.$key" ).string }
    fun block( key: String ): String { return TranslatableText( "block.$MOD_NAME.$key" ).string }

    object Advancement {

        fun title( key: String ): MutableText { return TranslatableText( "$MOD_NAME.advancements.$key.title" ) }
        fun description( key: String ): MutableText { return TranslatableText( "$MOD_NAME.advancements.$key.description" ) }

    }

}
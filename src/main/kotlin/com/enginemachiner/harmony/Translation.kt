package com.enginemachiner.harmony

import net.minecraft.text.Text
import net.minecraft.util.Language

object Translation {

    fun has( key: String ): Boolean { return get(key) != "$MOD_NAME.$key" }
    fun get( key: String ): String { return Text.translatable( "$MOD_NAME.$key" ).string }
    fun item( key: String ): String { return Text.translatable( "item.$MOD_NAME.$key" ).string }
    fun block( key: String ): String { return Text.translatable( "block.$MOD_NAME.$key" ).string }

    object AdvancementKey {

        fun title( key: String ): String { return "$MOD_NAME.advancements.$key.title" }
        fun description( key: String ): String { return "$MOD_NAME.advancements.$key.description" }

    }

}
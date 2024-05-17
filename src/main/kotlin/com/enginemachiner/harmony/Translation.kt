package com.enginemachiner.harmony

import net.minecraft.text.Text
import net.minecraft.util.Language

object Translation {

    private val language: Language = Language.getInstance()

    fun has( key: String ): Boolean { return language.hasTranslation("$MOD_NAME.$key") }
    fun get( key: String ): String { return Text.translatable( "$MOD_NAME.$key" ).string }
    fun item( key: String ): String { return Text.translatable( "item.$MOD_NAME.$key" ).string }
    fun block( key: String ): String { return Text.translatable( "block.$MOD_NAME.$key" ).string }

}
package com.enginemachiner.harmony

import net.minecraft.text.Text
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting

/** Provides chat message formatting utilities for the mod. */
open class Chat( private val mod: Mod ) {

    /** Creates a formatted title prefix for chat messages. */
    open fun title(): MutableText {

        val name = mod.name.uppercase();            val gray = Formatting.GRAY

        return Text.literal("[").append(name).append("]: ").formatted(gray)

    }

}
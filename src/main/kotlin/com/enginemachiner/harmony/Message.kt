package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

//** Sends messages and warnings to players and server. */
open class Message( var message: String, var player: PlayerEntity? = null ) {

    init { parse() }

    /** Parses translations using #. */
    fun parse() { message = Companion.parse(message) }

    private fun print() {

        val message = message.replace( regex, "" )

        modPrint(message)

    }

    open fun send( actionBar: Boolean = false ) {

        val player = player;        if ( player == null ) { print(); return }


        var message = message

        if ( !actionBar ) message = CHAT_MOD_TITLE + message


        player.sendMessage( Text.of(message), actionBar )

    }

    companion object {

        private val regex = Regex("§.")

        /** Parses translations using #. */
        fun parse(message: String): String {

            val list = message.split("/@")

            val parsed = mutableListOf<String>()

            for ( s in list ) {

                var s = s;      val has = Translation.has(s)

                if (has) s = Translation.get(s);        parsed.add(s)

            }

            return parsed.joinToString("")

        }

    }

}
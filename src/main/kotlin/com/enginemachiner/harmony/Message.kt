package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

object Message {

    /** Parses translations using #. */
    fun parse( toParse: String ): String {

        val list = toParse.split('#')

        val parsed = mutableListOf<String>()

        for ( s in list ) {

            var s = s;      val has = Translation.has(s)

            if (has) s = Translation.get(s);        parsed.add(s)

        }

        return parsed.joinToString("")

    }

    fun sendMessage( player: PlayerEntity?, msg: String, actionBar: Boolean = false ) {

        if ( player == null ) { modPrint(msg); return }

        var msg = msg;      if ( !actionBar ) msg = CHAT_MOD_TITLE + msg

        player.sendMessage( Text.of(msg), actionBar )

    }

}

/** Warns the user / player through the server. */
fun warnUser( player: PlayerEntity?, toParse: String, actionBar: Boolean = false ) {

    var toParse = Message.parse(toParse)

    if ( player == null ) toParse = toParse.replace( Regex("§."), "" )

    Message.sendMessage( player, toParse, actionBar )

}
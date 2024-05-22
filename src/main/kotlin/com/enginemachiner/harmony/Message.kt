package com.enginemachiner.harmony

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import java.lang.Exception

private object Message {

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

/** Warns user / player and displays a check console message. Mostly used when there's errors. */
@Environment(EnvType.CLIENT)
fun warnConsole( msg: String ) {

    warnUser(msg);      warnUser("message.check_console")

}

/** Warns console and prints exception stack trace. */
@Environment(EnvType.CLIENT)
fun warnConsole( exception: Exception, msg: String ) {

    warnUser(msg);      warnUser( exception, "message.check_console" )

}

/** Warns user using translations. It does not use the action bar. */
@Environment(EnvType.CLIENT)
fun warnUser( toParse: String ) {

    val msg = Message.parse(toParse);       warnPlayer(msg)

}

/** Warns user and prints exception stack trace. */
@Environment(EnvType.CLIENT)
fun warnUser( exception: Exception, toParse: String ) {

    warnUser(toParse);      exception.printStackTrace()

}

@Environment(EnvType.CLIENT)
fun warnPlayer( msg: String, actionBar: Boolean = false ) {

    val player = client().player

    Message.sendMessage( player, msg, actionBar )

}
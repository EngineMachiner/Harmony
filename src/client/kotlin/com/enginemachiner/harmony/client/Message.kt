package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Message

/** Warns user / player and displays a check console message. Mostly used when there's errors. */
fun warnConsole( msg: String ) {

    warnUser(msg);      warnUser("message.check_console")

}

/** Warns console and prints exception stack trace. */
fun warnConsole( exception: Exception, msg: String ) {

    warnUser(msg);      warnUser( exception, "message.check_console" )

}

/** Warns user using translations. It does not use the action bar. */
fun warnUser( toParse: String ) {

    val msg = Message.parse(toParse);       warnPlayer(msg)

}

/** Warns user and prints exception stack trace. */
fun warnUser( exception: Exception, toParse: String ) {

    warnUser(toParse);      exception.printStackTrace()

}

fun warnPlayer( msg: String, actionBar: Boolean = false ) {

    val player = client().player

    Message.sendMessage( player, msg, actionBar )

}
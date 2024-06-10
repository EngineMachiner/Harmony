package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Message

//** Sends messages and warnings to the client. */
class Message( message: String,     private val exception: Exception? = null ) : Message( message, player() ) {

    private fun exception() { exception?.printStackTrace() }

    override fun send( actionBar: Boolean ) { super.send(actionBar);      exception() }

    /** Warns user to check the console and sends the message after it. */
    fun console() { sendMessage("message.check_console"); send() }

}

fun sendMessage( message: String, exception: Exception? = null ) { Message(message, exception).send() }
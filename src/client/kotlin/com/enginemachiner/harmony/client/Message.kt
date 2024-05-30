package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Message

//** Sends messages and warnings to the client. */
class Message( message: String,     private val exception: Exception? ) : Message( message, player() ) {

    private fun exception() { exception?.printStackTrace() };           fun console() { send() }

    override fun send( actionBar: Boolean ) { super.send(actionBar);      exception() }

}
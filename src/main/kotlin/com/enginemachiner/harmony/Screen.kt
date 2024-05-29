package com.enginemachiner.harmony

fun registerCloseScreenReceiver() {

    val id = modID("close_screen")

    Receiver(id).register { server, sender, _ ->

        serverSend(server) { sender.closeHandledScreen() }

    }

}
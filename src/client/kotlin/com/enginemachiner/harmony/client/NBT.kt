package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.NBT.netID
import com.enginemachiner.harmony.client.Network.hasHandler
import net.minecraft.nbt.NbtCompound

object NBT {

    @JvmStatic /** Send and update NBT to the server. */
    fun send( nbt: NbtCompound ) {

        if ( !hasHandler() ) return


        val sender = Sender(netID) { it.write(nbt) }

        sender.toServer()

    }

}

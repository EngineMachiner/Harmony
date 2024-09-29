package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.NBT.netID
import com.enginemachiner.harmony.client.Network.hasHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

object NBT {

    @JvmStatic /** Send and update NBT to the server. */
    fun send( tag: CompoundTag ) {

        if ( !hasHandler() ) return


        val sender = Sender(netID) { it.write(tag) }

        sender.toServer()

    }

    /** Stores the display temporally for it to be sent later. */
    fun saveDisplay( stack: ItemStack, next: CompoundTag ) {

        if ( next.contains("resetDisplay") ) return;        val former = stack.tag!!

        next.put( "display", former.getCompound("display") )

    }

}
package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.NBT
import com.enginemachiner.harmony.client.Network.hasHandler
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.DataComponentTypes.CUSTOM_NAME
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

object NBT {

    @JvmStatic /** Send and update NBT to the server. */
    fun send( nbt: NbtCompound ) {

        if ( !hasHandler() ) return


        val payload = NBT.StackPayload(nbt)

        Sender(payload).toServer()

    }

    /** Stores the display temporally for it to be sent later. */
    fun saveDisplay( stack: ItemStack, next: NbtCompound ) {

        if ( next.contains("resetDisplay") ) return;        val former = stack.get(CUSTOM_NAME)!!

        next.putString( "display", former.string )

    }

}

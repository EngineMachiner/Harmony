package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.NBT
import net.minecraft.item.ItemStack

/** Stores the objects of networked stacks and updates each NBT when searched. */
class NetStacks {

    private val stacks = mutableListOf<ItemStack>()

    fun find( netStack: ItemStack ): ItemStack {

        if ( netStack.isEmpty ) return netStack


        val stack = stacks.find { NBT.equals( it, netStack ) }


        val update = update( netStack, stack )

        if (update) return stack!!


        stacks.add(netStack);           return netStack

    }

    /** Update the NBT. */
    private fun update( netStack: ItemStack, stack: ItemStack? ): Boolean {

        stack ?: return false;          stack.tag = netStack.tag

        return true

    }

}
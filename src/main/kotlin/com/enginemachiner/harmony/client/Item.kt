package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.NBT
import com.enginemachiner.harmony.NBT.nbt
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

interface ColorItem {

    private fun color(stack: ItemStack): Int {

        if ( !NBT.has(stack) ) return 0xFFFFFF

        return nbt(stack).getInt("Color")

    }

    fun registerColorProvider( registered: Item ) {

        ColorProviderRegistry.ITEM.register( { stack, _ -> color(stack) }, registered )

    }

}
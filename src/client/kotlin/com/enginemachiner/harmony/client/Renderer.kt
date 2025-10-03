package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.ColorItem
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import java.awt.Color

fun setShaderColor( color: Color ) {

    val color = color.getRGBComponents(null);           color.forEachIndexed { i, value -> color[i] = value / 255f }

    RenderSystem.setShaderColor( color[0], color[1], color[2], color[3] )

}

object ItemRenderer {

    /** Register item color provider. **/
    fun register( item: ColorItem ) {

        val provider = ItemColorProvider { stack, _ -> item.color(stack) }

        ColorProviderRegistry.ITEM.register( provider, item as Item )

    }

    val registry: BuiltinItemRendererRegistry = INSTANCE

    /** Register a dynamic renderer for an item. **/
    fun register( item: ItemConvertible, renderer: DynamicItemRenderer ) { registry.register( item, renderer ) }

}
package com.enginemachiner.harmony.client

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack

private typealias itemRegistry = BuiltinItemRendererRegistry
private typealias itemDynamic = BuiltinItemRendererRegistry.DynamicItemRenderer
private typealias itemRender = (ItemStack, ModelTransformation.Mode, MatrixStack, VertexConsumerProvider, Int, Int ) -> Unit

object Renderer {

    object Item {

        val registry: itemRegistry = BuiltinItemRendererRegistry.INSTANCE

        fun register( item: ItemConvertible, renderer: itemDynamic ) { registry.register( item, renderer ) }

        fun create( render: itemRender ): itemDynamic {

            return BuiltinItemRendererRegistry.DynamicItemRenderer(render)

        }

    }

}
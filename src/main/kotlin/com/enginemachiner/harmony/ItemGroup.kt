package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

interface ItemGroupData : ModID {

    val id: Identifier
    val itemGroup: ItemGroup;           val item: Item

}

object ModItemGroup : ItemGroupData {

    override val id = modID("item_group")

    override val itemGroup: ItemGroup = FabricItemGroupBuilder.create(id)
        .icon { item.defaultStack }.build()

    override val item = ItemGroupItem()

    class ItemGroupItem : Item( Settings() ) {

        override fun className(): String { return "item_group" }

    }

}
package com.enginemachiner.harmony

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

interface ItemGroupData : ModID {

    val id: Identifier
    val itemGroup: ItemGroup;           val item: Item

}

object ModItemGroup : ItemGroupData {

    override val id = modID("item_group")

    override val itemGroup: ItemGroup = FabricItemGroup.builder(id)
        .icon { item.defaultStack }.build()

    override val item = object : Item( Settings() ) {}

}
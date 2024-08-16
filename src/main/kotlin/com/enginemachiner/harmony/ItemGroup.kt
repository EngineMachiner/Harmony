package com.enginemachiner.harmony

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier

interface ItemGroupData : ModID {

    val id: Identifier;                 val key: RegistryKey<ItemGroup>
    val itemGroup: ItemGroup;           val item: Item

}

object ModItemGroup : ItemGroupData {

    override val id = modID("item_group")

    override val key: RegistryKey<ItemGroup> = RegistryKey.of( RegistryKeys.ITEM_GROUP, id )

    override val itemGroup: ItemGroup = FabricItemGroup.builder()
        .displayName( Text.of(MOD_TITLE) )
        .icon { item.defaultStack }.build()

    override val item = object : Item( Settings() ) {}

}
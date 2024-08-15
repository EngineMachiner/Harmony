package com.enginemachiner.harmony

import com.enginemachiner.harmony.ModItemGroup.itemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import kotlin.reflect.KClass

object Register {

    private fun group( item: Item ) {

        if ( item == itemGroup ) return;        val key = ModItemGroup.key

        ItemGroupEvents.modifyEntriesEvent(key).register { it.add(item) }

    }

    fun group( group: ItemGroup ): ItemGroup {

        return Registry.register( Registries.ITEM_GROUP, ModItemGroup.key, group )

    }

    fun block( block: Block, settings: Item.Settings ): Block {

        val name = ( block as ModID ).className();      val id = modID(name)


        val block = Registry.register( Registries.BLOCK, id, block )

        val item = BlockItem( block, settings );    Registry.register( Registries.ITEM, id, item )

        group(item)


        return block

    }

    fun item( item: Item ): Item {

        val id = ( item as ModID ).classID();       group(item)

        return Registry.register( Registries.ITEM, id, item )

    }

    fun sound( path: String ): SoundEvent {

        val id = modID(path);      val event = SoundEvent.of(id)

        return Registry.register( Registries.SOUND_EVENT, id, event )

    }

    fun enchantment( enchantment: Enchantment ): Enchantment {

        val path = ( enchantment as ModID ).classID()

        return Registry.register( Registries.ENCHANTMENT, path, enchantment )

    }

    fun fuel( kClass: KClass<*>, time: Int ) {

        FuelRegistry.INSTANCE.add( modItem(kClass), time )

    }

}
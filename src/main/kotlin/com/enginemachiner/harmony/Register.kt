package com.enginemachiner.harmony

import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry
import kotlin.reflect.KClass

object Register {

    fun block( block: Block, settings: Item.Settings ): Block {

        val name = ( block as ModID ).className();      val id = modID(name)


        val block = Registry.register( Registry.BLOCK, id, block )

        val item = BlockItem( block, settings );    Registry.register( Registry.ITEM, id, item )


        return block

    }

    fun item( item: Item ): Item {

        val id = ( item as ModID ).classID()

        return Registry.register( Registry.ITEM, id, item )

    }

    fun sound( path: String ): SoundEvent {

        val id = modID(path);      val event = SoundEvent(id)

        return Registry.register( Registry.SOUND_EVENT, id, event )

    }

    fun enchantment( enchantment: Enchantment ): Enchantment {

        val path = ( enchantment as ModID ).classID()

        return Registry.register( Registry.ENCHANTMENT, path, enchantment )

    }

    fun fuel( kClass: KClass<*>, time: Int ) {

        FuelRegistry.INSTANCE.add( modItem(kClass), time )

    }

}
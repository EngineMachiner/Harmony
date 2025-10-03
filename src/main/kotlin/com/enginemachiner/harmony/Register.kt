package com.enginemachiner.harmony

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.particle.DefaultParticleType
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry.BLOCK
import net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE
import net.minecraft.util.registry.Registry.ENCHANTMENT
import net.minecraft.util.registry.Registry.ITEM
import net.minecraft.util.registry.Registry.PARTICLE_TYPE
import net.minecraft.util.registry.Registry.SOUND_EVENT
import net.minecraft.util.registry.Registry.register

private typealias BlockEntityConstructor = (BlockPos, BlockState) -> BlockEntity

class Register( private val mod: Mod ) {

    private fun id( path: String ) = mod.id(path)

    fun item( name: String, item: Item ): Item {

        val id = id(name);            return register( ITEM, id, item )

    }

    data class RegisteredBlock( val block: Block, val item: Item )

    fun block( name: String, block: Block, settings: Item.Settings ): RegisteredBlock {

        val id = id(name);            val block = register( BLOCK, id, block )

        val item = BlockItem( block, settings );            register( ITEM, id, item )

        return RegisteredBlock( block, item )

    }

    fun blockEntity( name: String, constructor: BlockEntityConstructor, vararg blocks: Block ): BlockEntityType<BlockEntity> {

        val id = id(name);          val type = FabricBlockEntityTypeBuilder.create( constructor, *blocks ).build()

        return register( BLOCK_ENTITY_TYPE, id, type )

    }

    fun sound( name: String ): SoundEvent {

        val id = id(name);          val event = SoundEvent(id)

        return register( SOUND_EVENT, id, event )

    }

    fun enchantment( name: String, enchantment: Enchantment ): Enchantment {

        val id = id(name);          return register( ENCHANTMENT, id, enchantment )

    }

    fun particle( name: String, particle: DefaultParticleType ) {

        val id = id(name);          register( PARTICLE_TYPE, id, particle )
    }

    fun fuel( item: Item, time: Int ) { FuelRegistry.INSTANCE.add( item, time ) }

}
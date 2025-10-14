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

/**
 * Handles registration for the mod.
 * Provides convenient methods to register items, blocks, entities, sounds, etc.
 */
class Register( private val mod: Mod ) {

    private fun id( path: String ) = mod.id(path)

    fun item( name: String, item: Item ): Item {

        val id = id(name);            return register( ITEM, id, item )

    }

    fun block( name: String, block: Block ): Block {

        val id = id(name);            return register( BLOCK, id, block )

    }

    /**
     * Registers a block entity type.
     *
     * @param name The block entity's registry name.
     * @param constructor The constructor for creating block entity instances.
     * @param blocks The blocks that can have this block entity.
     * @return The registered block entity type.
     */
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

    /** Registers an item as fuel. The burn time is in ticks. */
    fun fuel( item: Item, time: Int ) { FuelRegistry.INSTANCE.add( item, time ) }

}
package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ToolItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.reflect.KClass

// Create item icon for the item group.
private var itemGroup: ItemGroup? = null

fun itemGroup(): ItemGroup? { return itemGroup }

object ItemGroup: Item( Settings() ), ModID {

    private val id = modID("item_group")

    init {

        itemGroup = FabricItemGroupBuilder.create(id)!!
            .icon { defaultStack }.build()

    }

}


val hands = arrayOf( Hand.MAIN_HAND, Hand.OFF_HAND )

fun handItem( player: PlayerEntity, kClass: KClass<*> ): ItemStack {

    return player.handItems.find { kClass.isInstance( it.item ) }!!

}


private val equipment = arrayOf( EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND )

fun breakEquipment( entity: LivingEntity, stack: ItemStack ) {

    val index = NBT.get(stack).getInt("Hand")

    entity.sendEquipmentBreakStatus( equipment[index] )

}


private val registry = Registry.ITEM

fun modItem( kClass: KClass<*> ): Item {

    val className = ModID.className(kClass)

    return modItem(className)

}

fun modItem(id: String): Item { return registry.get( modID(id) ) }

fun isModItem(stack: ItemStack): Boolean {

    return registry.getId( stack.item ).namespace == MOD_NAME

}


fun modItemSettings( maxCount: Int = 1 ): Item.Settings {

    return Item.Settings().group(itemGroup).maxCount(maxCount)

}


interface StackScreen {

    fun canOpenScreen( player: PlayerEntity, stack: ItemStack ): Boolean {

        val handStack = player.handItems.find { it != stack }!!

        return handStack.item is AirBlockItem

    }

}


interface Trackable {

    fun tick( stack: ItemStack, world: World, entity: Entity, slot: Int ) {

        trackHolder(stack, entity)

        if ( world.isClient ) return;       trackTick(stack, slot)

        if ( !NBT.has(stack) ) setupNBT(stack);         clean(stack)

    }


    fun trackHolder( stack: ItemStack, holder: Entity ) { Companion.trackHolder(stack, holder) }

    fun trackTick( stack: ItemStack, slot: Int ) {}


    fun getSetupNBT(stack: ItemStack): NbtCompound

    fun setupNBT(stack: ItemStack) {

        val nbt = stack.nbt!!;      nbt.put( MOD_NAME, getSetupNBT(stack) )

    }


    /** Clean unnecessary data if the player has the stack. */
    private fun clean(stack: ItemStack) {

        val nbt = NBT.get(stack);       if ( !nbt.contains("BlockPos") ) return

        nbt.remove("BlockPos");     nbt.remove("Slot")

    }

    companion object {

        fun trackHolder( stack: ItemStack, holder: Entity ) {

            if ( stack.holder == holder ) return

            stack.holder = holder

        }

    }

}


abstract class ToolItem( material: ToolMaterial, settings: Settings ) : ToolItem( material, settings ), Trackable, ModID {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ): Boolean { return false }

    override fun inventoryTick( stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean ) {
        tick( stack, world, entity, slot )
    }

}

abstract class Item(settings: Settings) : Item(settings), Trackable, ModID {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ): Boolean { return false }

    override fun inventoryTick( stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean ) {
        tick( stack, world, entity, slot )
    }

}
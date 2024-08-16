package com.enginemachiner.harmony

import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.item.Item
import net.minecraft.item.ToolItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.awt.Color
import kotlin.random.Random
import kotlin.reflect.KClass

val hands = arrayOf( Hand.MAIN_HAND, Hand.OFF_HAND )

fun handItem( player: PlayerEntity, kClass: KClass<*> ): ItemStack {

    return player.handItems.find { kClass.isInstance( it.item ) }!!

}


private val equipment = arrayOf( EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND )

fun breakEquipment( entity: LivingEntity, stack: ItemStack ) {

    val index = NBT.nbt(stack).getInt("Hand")

    entity.sendEquipmentBreakStatus( equipment[index] )

}


private val registry = Registries.ITEM

fun modItem( kClass: KClass<*> ): Item {

    val className = ModID.className(kClass);        return modItem(className)

}

fun modItem(id: String): Item { val id = modID(id);     return registry.get(id) }

fun isModItem(stack: ItemStack): Boolean {

    return registry.getId( stack.item ).namespace == MOD_NAME

}


fun modItemSettings( maxCount: Int = 1 ): Item.Settings {

    return Item.Settings().maxCount(maxCount)

}


interface ColorItem {

    /** Puts the color in the NBT. */
    fun setColor( nbt: NbtCompound ): NbtCompound {

        nbt.putInt( "Color", color().rgb );     return nbt

    }

    open fun color(): Color {

        val h = Random.nextInt( 100 + 1 ) * 0.01f
        val b = Random.nextInt( 75, 100 + 1 ) * 0.01f

        return Color.getHSBColor( h, 0.35f, b )

    }

}


interface StackScreen {

    fun canOpenScreen( player: PlayerEntity, stack: ItemStack ): Boolean {

        val handStack = player.handItems.find { it != stack }!!

        return handStack.item is AirBlockItem

    }

}


/** Gets the stack holder as a player. */
fun player( stack: ItemStack ): PlayerEntity { return stack.holder as PlayerEntity }

/** Damage the stack. */
fun damage( stack: ItemStack, damage: Int = 1, entity: LivingEntity = player(stack) ) {

    stack.damage( damage, entity ) { breakEquipment( it, stack ) }

}

/** Adds tracking and NBT setup to items. */
interface HarmonyItem {

    fun tick( stack: ItemStack, world: World, entity: Entity, slot: Int ) {

        trackHolder(stack, entity)

        if ( world.isClient ) return;       trackTick(stack, slot)

        if ( !NBT.has(stack) ) setupNBT(stack);         clean(stack)

    }


    fun trackHolder( stack: ItemStack, holder: Entity ) { Companion.trackHolder(stack, holder) }

    fun trackTick( stack: ItemStack, slot: Int ) {}


    open fun getSetupNBT( stack: ItemStack ): NbtCompound { return NbtCompound() }

    fun setupNBT(stack: ItemStack) {

        val nbt = stack.nbt!!;      nbt.put( MOD_NAME, getSetupNBT(stack) )

    }


    /** Clean unnecessary data if the player has the stack. */
    open fun clean(stack: ItemStack) {

        val nbt = NBT.nbt(stack);       if ( !nbt.contains("BlockPos") ) return

        nbt.remove("BlockPos");     nbt.remove("Slot")

    }

    companion object {

        fun trackHolder( stack: ItemStack, holder: Entity? ) {

            if ( stack.holder == holder ) return

            stack.holder = holder

        }

    }

}


private interface Harmony : HarmonyItem, ModID

abstract class ToolItem( material: ToolMaterial, settings: Settings ) : ToolItem( material, settings ), Harmony {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ): Boolean { return false }

    override fun inventoryTick( stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean ) { tick( stack, world, entity, slot ) }

}

abstract class Item(settings: Settings) : Item(settings), Harmony {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ): Boolean { return false }

    override fun inventoryTick( stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean ) { tick( stack, world, entity, slot ) }

}
package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

fun slotIndex( slots: DefaultedList<Slot>, stack: ItemStack ): Int? {

    val stack = slots.find { it.stack == stack } ?: return null

    return slots.indexOf(stack)

}

fun inventoryList( inventory: Inventory ): List<ItemStack> {

    val list = mutableListOf<ItemStack>()

    for ( i in 0 until inventory.size() ) list.add( inventory.getStack(i) )

    return list

}

/** It's just a custom inventory. */
interface HarmonyInventory : SidedInventory {

    fun items(): DefaultedList<ItemStack>

    override fun getAvailableSlots(side: Direction): IntArray {

        val result = IntArray( size() );       for ( i in result.indices ) result[i] = i

        return result

    }

    override fun canInsert( slot: Int, stack: ItemStack, direction: Direction? ): Boolean { return true }

    override fun canExtract( slot: Int, stack: ItemStack, direction: Direction ): Boolean { return true }

    override fun size(): Int { return items().size }

    override fun isEmpty(): Boolean {

        for ( i in 0 until size() ) if ( !getStack(i).isEmpty ) return false

        return true

    }

    override fun getStack(slot: Int): ItemStack { return items()[slot] }

    override fun removeStack( slot: Int, amount: Int ): ItemStack {

        val result = Inventories.splitStack( items(), slot, amount )

        if ( !result.isEmpty ) markDirty();         return result

    }

    override fun removeStack(slot: Int): ItemStack {

        return Inventories.removeStack( items(), slot )

    }

    override fun setStack( slot: Int, stack: ItemStack ) {

        items()[slot] = stack;          val max = maxCountPerStack

        if ( stack.count > max ) stack.count = max

    }

    override fun clear() { items().clear() }

    override fun canPlayerUse( player: PlayerEntity ): Boolean { return true }

}

open class StackInventory( val stack: ItemStack, size: Int ) : HarmonyInventory {

    private val items = DefaultedList.ofSize( size, ItemStack.EMPTY )

    init {

        val nbt = stack.getSubNbt("Items")

        if ( nbt != null ) Inventories.readNbt( nbt, items )

    }

    override fun items(): DefaultedList<ItemStack> { return items }

    override fun markDirty() {

        val nbt = stack.getOrCreateSubNbt("Items")

        Inventories.writeNbt( nbt, items )

    }

}
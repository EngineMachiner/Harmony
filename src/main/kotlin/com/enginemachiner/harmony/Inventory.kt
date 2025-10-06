package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

fun Inventory.toList(): List<ItemStack> {

    val range = 0 until size();         return range.map { getStack(it) }

}

interface HarmonyInventory : SidedInventory {

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    fun items(): DefaultedList<ItemStack>

    override fun clear() { items().clear() }

    override fun size() = items().size

    override fun isEmpty() = items().all { it.isEmpty }

    override fun getStack( slot: Int ) = items()[slot]

    override fun getAvailableSlots( side: Direction ): IntArray {

        val size = size();          return IntArray(size) { i -> i }

    }

    override fun canPlayerUse( player: PlayerEntity ) = true

    override fun canInsert( slot: Int, stack: ItemStack, direction: Direction? ) = true
    override fun canExtract( slot: Int, stack: ItemStack, direction: Direction ) = true

    override fun setStack( slot: Int, stack: ItemStack ) {

        val max = maxCountPerStack;             items()[slot] = stack

        if ( stack.count > max ) stack.count = max

    }

    override fun removeStack( slot: Int, amount: Int ): ItemStack {

        return Inventories.splitStack( items(), slot, amount )

    }

    override fun removeStack( slot: Int ): ItemStack {

        return Inventories.removeStack( items(), slot )

    }

}

/**
 * An inventory stored within an ItemStack's NBT data.
 * Useful for items that contain other items (e.g., backpacks, shulker boxes).
 *
 * @param stack The ItemStack that stores this inventory.
 * @param size The number of slots in this inventory.
 */
open class StackInventory( val stack: ItemStack, size: Int ) : HarmonyInventory {

    private val items = DefaultedList.ofSize( size, ItemStack.EMPTY )

    init {

        val nbt = stack.getSubNbt("Items")

        if ( nbt != null ) Inventories.readNbt( nbt, items )

    }

    override fun items(): DefaultedList<ItemStack> = items

    override fun markDirty() {

        val nbt = stack.getOrCreateSubNbt("Items");         Inventories.writeNbt( nbt, items )

    }

}
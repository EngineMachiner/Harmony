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

    /**
     * Checks if the inventory is empty.
     * @return true if this inventory has only empty stacks, false otherwise.
     */
    override fun isEmpty() = items().all { it.isEmpty }

    override fun getStack( slot: Int ) = items()[slot]

    override fun getAvailableSlots( side: Direction ): IntArray {

        val size = size();          return IntArray(size) { i -> i }

    }

    override fun canPlayerUse( player: PlayerEntity ) = true

    override fun canInsert( slot: Int, stack: ItemStack, direction: Direction? ) = true
    override fun canExtract( slot: Int, stack: ItemStack, direction: Direction ) = true

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     * @param slot  The inventory slot of which to replace the item stack.
     * @param stack The replacing item stack. If the stack is too big for
     *              this inventory max count,
     *              it gets resized to this inventory's maximum amount.
     */
    override fun setStack( slot: Int, stack: ItemStack ) {

        val max = maxCountPerStack;             items()[slot] = stack

        if ( stack.count > max ) stack.count = max

    }

    /**
     * Removes items from an inventory slot.
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     *              takes all items in that slot.
     */
    override fun removeStack( slot: Int, amount: Int ): ItemStack {

        return Inventories.splitStack( items(), slot, amount )

    }

    /**
     * Removes all items from an inventory slot.
     * @param slot The slot to remove from.
     */
    override fun removeStack( slot: Int ): ItemStack {

        return Inventories.removeStack( items(), slot )

    }

}

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
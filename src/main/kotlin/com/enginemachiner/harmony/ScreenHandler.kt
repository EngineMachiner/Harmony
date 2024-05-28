package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

const val SLOT_SIZE = 18f

private typealias Condition = (ItemStack) -> Boolean

abstract class HarmonyScreenHandler( type: ScreenHandlerType<*>, syncID: Int ) : ScreenHandler( type, syncID ), SlotFactory {

    /** Get the slot index with the swap button. */
    fun slotIndex( button: Int ): Int { return slots.size - 1 + button - 8 }

    private fun internalSwap( from: ItemStack, to: ItemStack, condition: Condition ): Boolean {

        val isAllowed = to.isEmpty || condition(to);        return condition(from) && isAllowed

    }

    fun canSwap( button: Int, slotIndex: Int, condition: Condition ): Boolean {

        val stack = stacks[slotIndex];          val buttonStack = stacks[ slotIndex(button) ]

        return internalSwap( buttonStack, stack, condition ) || internalSwap( stack, buttonStack, condition )

    }

    fun canPickUp( onSlots: Boolean, condition: Condition ): Boolean {

        val cursor = condition(cursorStack) || cursorStack.isEmpty

        return cursor && onSlots

    }

    fun insertItem( i: Int, start: Int, limit: Int, end: Int = slots.size ): Boolean {

        val stack = slots[i].stack

        return i >= limit && insertItem( stack, start, limit, false )
                || insertItem( stack, limit, end, true )

    }

}

interface SlotFactory {

    fun playerSlots( x: Float, y: Float, inventory: PlayerInventory ): List<Slot> {

        val slots1 = slots( 3, 9, x, y, inventory, 9 )


        val y = y + SLOT_SIZE * 3 + 4

        val slots2 = slots( 1, 9, x, y, inventory )


        return slots1 + slots2

    }

    fun slots(

        rows: Int,  columns: Int,       x: Float,   y: Float,

        inventory: Inventory,           start: Int = 0

    ): List<Slot> {

        val slots = mutableListOf<Slot>()

        if ( rows < 1 || columns < 1 ) { modPrint( INVALID_SLOTS + this );      return slots }


        for ( i in 0 ..< rows ) { for ( j in 0 ..< columns ) {

            val x = SLOT_SIZE * j + x;     val y = SLOT_SIZE * i + y

            val index = start + j + i * columns


            val slot = slot( x, y, inventory, index )

            slots.add(slot)

        } }


        return slots

    }

    fun slot( x: Float, y: Float, inventory: Inventory, index: Int ): Slot {

        val slot = Slot( inventory, index, x.toInt(), y.toInt() )

        return slot

    }

    private companion object {

        const val INVALID_SLOTS = "Invalid rows or columns for: "

    }

}
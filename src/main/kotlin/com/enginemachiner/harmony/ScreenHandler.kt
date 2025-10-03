package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

const val SLOT_SIZE = 18f

abstract class HarmonyScreenHandler( type: ScreenHandlerType<*>, syncID: Int ) : ScreenHandler( type, syncID ) {

    fun playerSlots( inventory: PlayerInventory, x: Float, y: Float ): List<Slot> {

        val main = slots( inventory, 9, 3, 9, x, y )

        val y = y + SLOT_SIZE * 3 + 4;          val hotbar = slots( inventory, 0, 1, 9, x, y )

        return main + hotbar

    }

    fun slots(

        inventory: Inventory,           startIndex: Int,
        rows: Int,                      columns: Int,
        x: Float,                       y: Float

    ): List<Slot> {

        return buildList {

            for ( row in 0 ..< rows ) { for ( column in 0 ..< columns ) {

                val index = startIndex + column + row * columns

                val x = SLOT_SIZE * column + x;         val y = SLOT_SIZE * row + y

                val slot = slot( inventory, index, x, y );          add(slot)

            } }

        }

    }

    fun addPlayerSlots( inventory: PlayerInventory, x: Float, y: Float ) {

        playerSlots( inventory, x, y ).forEach { addSlot(it) }

    }

    fun addSlots(

        inventory: Inventory,           startIndex: Int,
        rows: Int,                      columns: Int,
        x: Float,                       y: Float

    ) {

        val slots = slots( inventory, startIndex, rows, columns, x, y )

        slots.forEach { addSlot(it) }

    }

    protected open fun slot( inventory: Inventory, index: Int, x: Float, y: Float ): Slot {

        return Slot( inventory, index, x.toInt(), y.toInt() )

    }

}
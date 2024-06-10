package com.enginemachiner.harmony

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object NBT {

    val netID = modID("network_nbt")

    @JvmStatic
    fun has(stack: ItemStack): Boolean { return stack.orCreateNbt.contains( MOD_NAME ) }

    @JvmStatic
    fun nbt(stack: ItemStack): NbtCompound { return stack.nbt!!.getCompound( MOD_NAME ) }

    fun id(stack: ItemStack): Int { return nbt(stack).getInt("ID") }

    /** Checks if stacks match the same IDs. */
    fun equals( stack1: ItemStack, stack2: ItemStack ): Boolean { return id(stack1) == id(stack2) }

    /** Checks if NBTs match the same IDs. */
    fun equals( stack: ItemStack, nbt: NbtCompound ): Boolean { return id(stack) == nbt.getInt("ID") }

    //fun blockPos(stack: ItemStack): BlockPos { return blockPos( get(stack) ) }

    private fun blockPos(nbt: NbtCompound): BlockPos {

        val strings = nbt.getString("BlockPos").replace( " ", "" ).split(',')

        val doubles = mutableListOf<Double>();   strings.forEach { doubles.add( it.toDouble() ) }

        val blockPos = BlockPos( doubles[0], doubles[1], doubles[2] )

        return blockPos

    }

    private fun blockStack( nbt: NbtCompound, world: World ): ItemStack? {

        if ( !nbt.contains("BlockPos") ) return null

        val slot = nbt.getInt("Slot");      val blockPos = blockPos(nbt)

        val inventory = world.getBlockEntity(blockPos) ?: return null

        inventory as Inventory;     return inventory.getStack(slot)

    }

    private fun getStack( player: PlayerEntity, nbt: NbtCompound ): ItemStack? {

        val stack = blockStack( nbt, player.world );        if ( stack != null ) return stack

        if ( nbt.contains("BlockPos") ) return null;        val inventory = player.inventory


        fun idStack(): ItemStack? {

            val inventory = inventoryList(inventory)

            return inventory.find { equals(it, nbt) }

        }

        when {

            nbt.contains("Hand") -> {

                val i = nbt.getInt("Hand");      val hand = hands[i]

                return player.getStackInHand(hand)

            }

            nbt.contains("Slot") -> {

                val i = nbt.getInt("Slot");      val stack = inventory.getStack(i)

                if ( stack.isEmpty ) return idStack();       return stack

            }

            nbt.contains("ID") -> return idStack()

            else -> return null

        }

    }


    private fun putInt( nbt: NbtCompound, key: String, value: Int ) {

        val b = !nbt.contains(key) || nbt.getInt(key) != value

        if (b) nbt.putInt( key, value )

    }


    fun trackPlayer( stack: ItemStack, key: String = "lastHolder" ) {

        val holder = stack.holder;      if ( holder !is PlayerEntity ) return

        val nbt = nbt(stack);           val uuid = holder.uuid


        val b = !nbt.containsUuid(key) || nbt.getUuid(key) != uuid

        if (b) nbt.putUuid( key, uuid )

    }

    fun trackHand(stack: ItemStack) {

        val holder = stack.holder;      if ( holder !is PlayerEntity ) return

        val i = holder.handItems.indexOf(stack)

        putInt( nbt(stack), "Hand", i )

    }

    fun trackSlot( stack: ItemStack, slot: Int ) {

        putInt( nbt(stack), "Slot", slot )

    }

    private fun checkDisplay( next: NbtCompound, stack: ItemStack ) {

        val former = stack.nbt!!

        if ( next.contains("display") ) {


            val display = next.getCompound("display")

            former.put( "display", display );      next.remove("display")


        } else if ( next.contains("resetDisplay") ) {

            stack.removeCustomName();   next.remove("resetDisplay")

        }

    }

    fun networking() {

        Receiver(netID).register { server, player, buf ->

            val next = buf.readNbt()!!

            serverSend(server) {

                val stack = getStack( player, next ) ?: return@serverSend

                val former = stack.nbt!!;      checkDisplay( next, stack )

                former.put( MOD_NAME, next )

            }

        }

    }

}

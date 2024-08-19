package com.enginemachiner.harmony

import io.netty.buffer.ByteBuf
import net.minecraft.component.DataComponentTypes.CUSTOM_DATA
import net.minecraft.component.DataComponentTypes.CUSTOM_NAME
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object NBT {

    val netID = modID("network_nbt")

    @JvmStatic
    fun customData(stack: ItemStack): NbtCompound { return stack.get(CUSTOM_DATA)!!.copyNbt()!! }

    @JvmStatic
    fun has(stack: ItemStack): Boolean { return customData(stack).contains(MOD_NAME) }

    @JvmStatic
    fun nbt(stack: ItemStack): NbtCompound { return customData(stack).getCompound(MOD_NAME) }

    fun id(stack: ItemStack): Int { return nbt(stack).getInt("ID") }

    /** Checks if stacks match the same IDs. */
    fun equals( stack1: ItemStack, stack2: ItemStack ): Boolean {

        return has(stack1) && has(stack2) && id(stack1) == id(stack2)

    }

    /** Checks if NBTs match the same IDs. */
    fun equals( stack: ItemStack, nbt: NbtCompound ): Boolean {

        return has(stack) && id(stack) == nbt.getInt("ID")

    }

    fun blockPos(stack: ItemStack): BlockPos { val nbt = nbt(stack);        return blockPos(nbt) }

    private fun blockPos(nbt: NbtCompound): BlockPos {

        val strings = nbt.getString("BlockPos").replace( " ", "" ).split(',')

        val ints = mutableListOf<Int>();   strings.forEach { ints.add( it.toInt() ) }

        val blockPos = BlockPos( ints[0], ints[1], ints[2] )

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

    fun equipment(stack: ItemStack): EquipmentSlot {

        val index = nbt(stack).getInt("Hand")

        return equipment[index]

    }

    private fun checkDisplay( next: NbtCompound, stack: ItemStack ) {

        if ( next.contains("display") ) {


            val display = next.getString("display")

            stack.set( CUSTOM_NAME, Text.of(display) );      next.remove("display")


        } else if ( next.contains("resetDisplay") ) {

            stack.remove(CUSTOM_NAME);   next.remove("resetDisplay")

        }

    }

    data class StackPayload( val data: NbtCompound ) : Payload() {

        override fun getId(): CustomPayload.Id<StackPayload> { return Companion.id }

        fun register() { playS2C.register( id, codec ) }

        companion object : PayloadCompanion() {

            override val id = CustomPayload.Id<StackPayload>(netID)
            override val codec: PacketCodec<ByteBuf, StackPayload> = PacketCodec.tuple( PacketCodecs.NBT_COMPOUND, StackPayload::data, ::StackPayload )

        }

    }

    fun networking() {

        Receiver( StackPayload ).register { payload, context ->

            val player = context.player();          val server = player.server

            serverSend(server) {

                payload as StackPayload;        val next = payload.data

                val stack = getStack( player, next ) ?: return@serverSend

                val former = customData(stack);      checkDisplay( next, stack )

                former.put( MOD_NAME, next )

            }

        }

    }

}

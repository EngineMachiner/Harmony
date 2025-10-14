package com.enginemachiner.harmony

import com.enginemachiner.harmony.Harmony.Identifiers.UPDATE_STACK_NBT
import com.enginemachiner.harmony.Harmony.harmonyUUID
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import java.util.UUID

object Harmony : Mod("Harmony") {

    class UpdateStackPacket( var slot: Int, var nbt: NbtCompound? ) : Packet {

        constructor(): this( 0, null )

        override fun read( buf: PacketByteBuf ) {

            slot = buf.readInt();             nbt = buf.readNbt()

        }

        override fun write(): PacketByteBuf {

            val buf = PacketByteBufs.create().writeNbt(nbt);          buf.writeInt(slot);         return buf

        }

    }

    object Identifiers {

        /** Identifier for the update stack NBT packet. */
        val UPDATE_STACK_NBT = id("update_stack_nbt")

    }

    fun NbtCompound.harmonyNBT() = nbt(this)

    fun NbtCompound.harmonyUUID(): UUID? {

        val hasUUID = containsUuid("UUID");         return if (hasUUID) harmonyNBT().getUuid("UUID") else null

    }

}

class ModInitializer : ModInitializer {

    override fun onInitialize() {

        ServerNetworking.receive( UPDATE_STACK_NBT, Harmony::UpdateStackPacket ) {

            server.execute {

                val slot = packet.slot;           val packetUUID = packet.nbt!!.harmonyUUID()

                val stack = sender.inventory.getStack(slot);            val uuid = stack.nbt?.harmonyUUID()

                if ( uuid != packetUUID ) return@execute;           stack.nbt = packet.nbt

            }

        }

    }

}
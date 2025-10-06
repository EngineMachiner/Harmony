package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Mod
import com.enginemachiner.harmony.Packet
import com.enginemachiner.harmony.id
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

/** Checks if the client has a network handler. */
fun MinecraftClient.isConnected() = networkHandler != null

/** Manages client-side networking for packet sending and receiving. */
class ClientNetworking( private val mod: Mod ) {

    private fun id( path: String ) = mod.id(path)

    /**
     * Registers a receiver for deserialized packets.
     *
     * @param path The packet identifier path.
     * @param packet The packet instance to deserialize into
     * @param handler The handler lambda with deserialized packet context
     */
    fun <T: Packet> receive( path: String, packet: T, handler: DeserializedContext<T>.() -> Unit ) {

        val id = id(path)

        ClientPlayNetworking.registerGlobalReceiver(id) { client, networkHandler, buf, packetSender ->

            packet.read(buf);           DeserializedContext( client, networkHandler, packet, packetSender ).handler()

        }

    }

    /** Sends a packet to the server. */
    fun send( path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write();               ClientPlayNetworking.send( id, buf )

    }


    /**
     * Registers a receiver for raw packet buffers.
     * @param buf The packet buffer (unused in registration, context provides actual buffer)
     * @param handler The handler lambda with raw buffer context
     */
    fun receive( path: String, buf: PacketByteBuf, handler: RawContext.() -> Unit ) {

        val id = id(path)

        ClientPlayNetworking.registerGlobalReceiver(id) { client, networkHandler, buf, packetSender ->

            RawContext( client, networkHandler, buf, packetSender ).handler()

        }

    }

    /** Sends a raw packet buffer to the server. */
    fun send( path: String, buf: PacketByteBuf ) {

        val id = id(path);              ClientPlayNetworking.send( id, buf )

    }

    companion object {

        data class RawContext(
            val client: MinecraftClient,            val networkHandler: ClientPlayNetworkHandler,
            val buf: PacketByteBuf,                 val packetSender: PacketSender
        )

        data class DeserializedContext<T>(
            val client: MinecraftClient,            val networkHandler: ClientPlayNetworkHandler,
            val packet: T,                          val packetSender: PacketSender
        )

    }

}
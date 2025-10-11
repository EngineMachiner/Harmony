package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Packet
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/** Checks if the client has a network handler. */
fun MinecraftClient.isConnected() = networkHandler != null

/** Manages client-side networking for packet sending and receiving. */
object ClientNetworking {

    /**
     * Registers a receiver for deserialized packets.
     *
     * @param id The packet identifier
     * @param packet The packet factory or constructor
     * @param handler The handler lambda with deserialized packet context
     */
    fun <T: Packet> receive( id: Identifier, packet: () -> T, handler: DeserializedContext<T>.() -> Unit ) {

        ClientPlayNetworking.registerGlobalReceiver(id) { client, networkHandler, buf, packetSender ->

            val packet = packet();          packet.read(buf)

            DeserializedContext( client, networkHandler, packet, packetSender ).handler()

        }

    }

    /** Sends a packet to the server. */
    fun send( id: Identifier, packet: Packet ) {

        val buf = packet.write();               ClientPlayNetworking.send( id, buf )

    }


    /**
     * Registers a receiver for raw packet buffers.
     *
     * @param id The packet identifier
     * @param buf The packet buffer (unused in registration, context provides actual buffer)
     * @param handler The handler lambda with raw buffer context
     */
    fun receive( id: Identifier, buf: PacketByteBuf, handler: RawContext.() -> Unit ) {

        ClientPlayNetworking.registerGlobalReceiver(id) { client, networkHandler, buf, packetSender ->

            RawContext( client, networkHandler, buf, packetSender ).handler()

        }

    }

    /** Sends a raw packet buffer to the server. */
    fun send( id: Identifier, buf: PacketByteBuf ) { ClientPlayNetworking.send( id, buf ) }

    /** Context for raw packet handlers with direct buffer access. */
    data class RawContext(
        val client: MinecraftClient,            val networkHandler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,                 val packetSender: PacketSender
    )

    /** Context for deserialized packet handlers with typed packet access. */
    data class DeserializedContext<T>(
        val client: MinecraftClient,            val networkHandler: ClientPlayNetworkHandler,
        val packet: T,                          val packetSender: PacketSender
    )

}
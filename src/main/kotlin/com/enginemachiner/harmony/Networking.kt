package com.enginemachiner.harmony

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/** Interface for serializable network packets. */
interface Packet {

    /** Reads packet data from the buffer. */
    fun read( buf: PacketByteBuf )

    /** Writes packet data to a buffer and returns it. */
    fun write(): PacketByteBuf

}

/** Manages server-side networking for packet sending and receiving. */
object ServerNetworking {

    /**
     * Registers a receiver for deserialized packets.
     *
     * @param id The packet identifier
     * @param packet The packet factory or constructor
     * @param handler The handler lambda with deserialized packet context
     */
    fun <T: Packet> receive( id: Identifier, packet: () -> T, handler: DeserializedContext<T>.() -> Unit ) {

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            val packet = packet();          packet.read(buf)

            DeserializedContext( server, sender, networkHandler, packet, packetSender ).handler()

        }

    }

    /** Sends a packet to a specific player. */
    fun sendTo( player: ServerPlayerEntity, id: Identifier, packet: Packet ) {

        val buf = packet.write();               ServerPlayNetworking.send( player, id, buf )

    }

    /** Broadcasts a packet to a set of players. */
    fun broadcast( players: Set<ServerPlayerEntity>, id: Identifier, packet: Packet ) {

        val buf = packet.write();           players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    /** Broadcasts a packet to all players on the server. */
    fun broadcast( server: MinecraftServer, id: Identifier, packet: Packet ) {

        val buf = packet.write();           val players = server.playerManager.playerList

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }


    /**
     * Registers a receiver for raw packet buffers.
     * Use this for simple packets or when you need direct buffer control.
     *
     * @param id The packet identifier
     * @param handler The handler to process the raw packet buffer.
     */
    fun receive( id: Identifier, handler: RawContext.() -> Unit ) {

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            RawContext( server, sender, networkHandler, buf, packetSender ).handler()

        }

    }

    /** Sends a raw packet buffer to a specific player. */
    fun sendTo( player: ServerPlayerEntity, id: Identifier, buf: PacketByteBuf ) {

        ServerPlayNetworking.send( player, id, buf )

    }

    /** Broadcasts a raw packet buffer to a set of players. */
    fun broadcast( players: Set<ServerPlayerEntity>, id: Identifier, buf: PacketByteBuf ) {

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    /** Broadcasts a raw packet buffer to all players on the server. */
    fun broadcast( server: MinecraftServer, id: Identifier, buf: PacketByteBuf ) {

        val players = server.playerManager.playerList;          players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    /** Context for raw packet handlers with direct buffer access. */
    data class RawContext(
        val server: MinecraftServer,                                val sender: ServerPlayerEntity,
        val networkHandler: ServerPlayNetworkHandler,               val buf: PacketByteBuf,
        val packetSender: PacketSender
    )

    /** Context for deserialized packet handlers with typed packet access. */
    data class DeserializedContext<T>(
        val server: MinecraftServer,                                val sender: ServerPlayerEntity,
        val networkHandler: ServerPlayNetworkHandler,               val packet: T,
        val packetSender: PacketSender
    )

}
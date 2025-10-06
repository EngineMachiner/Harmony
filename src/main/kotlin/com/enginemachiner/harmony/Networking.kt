package com.enginemachiner.harmony

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

/** Interface for serializable network packets. */
interface Packet {

    /** Reads packet data from the buffer. */
    fun read( buf: PacketByteBuf )

    /** Writes packet data to a buffer and returns it. */
    fun write(): PacketByteBuf

}

/** Manages server-side networking for packet sending and receiving. */
class ServerNetworking( private val mod: Mod ) {

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

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            packet.read(buf);           DeserializedContext( server, sender, networkHandler, packet, packetSender ).handler()

        }

    }

    /** Sends a packet to a specific player. */
    fun sendTo( player: ServerPlayerEntity, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write();               ServerPlayNetworking.send( player, id, buf )

    }

    /** Broadcasts a packet to a set of players. */
    fun broadcast( players: Set<ServerPlayerEntity>, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write()

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    /** Broadcasts a packet to all players on the server. */
    fun broadcast( server: MinecraftServer, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write();           val players = server.playerManager.playerList

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }


    /**
     * Registers a receiver for raw packet buffers.
     * Use this for simple packets or when you need direct buffer control.
     *
     * @param path The packet identifier path.
     * @param handler The handler to process the raw packet buffer.
     */
    fun receive( path: String, handler: RawContext.() -> Unit ) {

        val id = id(path)

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            RawContext( server, sender, networkHandler, buf, packetSender ).handler()

        }

    }

    /** Sends a raw packet buffer to a specific player. */
    fun sendTo( player: ServerPlayerEntity, path: String, buf: PacketByteBuf ) {

        val id = id(path);              ServerPlayNetworking.send( player, id, buf )

    }

    /** Broadcasts a raw packet buffer to a set of players. */
    fun broadcast( players: Set<ServerPlayerEntity>, path: String, buf: PacketByteBuf ) {

        val id = id(path);              players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    /** Broadcasts a raw packet buffer to all players on the server. */
    fun broadcast( server: MinecraftServer, path: String, buf: PacketByteBuf ) {

        val id = id(path);              val players = server.playerManager.playerList

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    companion object {

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

}
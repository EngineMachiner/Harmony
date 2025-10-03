package com.enginemachiner.harmony

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

interface Packet {

    fun read( buf: PacketByteBuf );             fun write(): PacketByteBuf

}

class ServerNetworking( private val mod: Mod ) {

    private fun id( path: String ) = mod.id(path)

    fun <T: Packet> receive( path: String, packet: T, handler: DeserializedContext<T>.() -> Unit ) {

        val id = id(path)

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            packet.read(buf);           DeserializedContext( server, sender, networkHandler, packet, packetSender ).handler()

        }

    }

    fun sendTo( player: ServerPlayerEntity, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write();               ServerPlayNetworking.send( player, id, buf )

    }

    fun broadcast( players: Set<ServerPlayerEntity>, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write()

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    fun broadcast( server: MinecraftServer, path: String, packet: Packet ) {

        val id = id(path);              val buf = packet.write();           val players = server.playerManager.playerList

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }


    // Use this one if you're using an empty PacketByteBuf.
    fun receive( path: String, handler: RawContext.() -> Unit ) {

        val id = id(path)

        ServerPlayNetworking.registerGlobalReceiver(id) { server, sender, networkHandler, buf, packetSender ->

            RawContext( server, sender, networkHandler, buf, packetSender ).handler()

        }

    }

    fun sendTo( player: ServerPlayerEntity, path: String, buf: PacketByteBuf ) {

        val id = id(path);              ServerPlayNetworking.send( player, id, buf )

    }

    fun broadcast( players: Set<ServerPlayerEntity>, path: String, buf: PacketByteBuf ) {

        val id = id(path);              players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    fun broadcast( server: MinecraftServer, path: String, buf: PacketByteBuf ) {

        val id = id(path);              val players = server.playerManager.playerList

        players.forEach { ServerPlayNetworking.send( it, id, buf ) }

    }

    companion object {

        data class RawContext(
            val server: MinecraftServer,                                val sender: ServerPlayerEntity,
            val networkHandler: ServerPlayNetworkHandler,               val buf: PacketByteBuf,
            val packetSender: PacketSender
        )

        data class DeserializedContext<T>(
            val server: MinecraftServer,                                val sender: ServerPlayerEntity,
            val networkHandler: ServerPlayNetworkHandler,               val packet: T,
            val packetSender: PacketSender
        )

    }

}
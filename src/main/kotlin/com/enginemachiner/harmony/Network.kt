package com.enginemachiner.harmony

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerTask
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

/** Maximum file data (bytes) that can be transferred to clients. */
const val MAX_BYTES = 32000

fun serverSend( server: MinecraftServer, runnable: Runnable ) {

    val task = ServerTask( server.ticks, runnable )

    server.send(task)

}

fun isNotSender( current: PlayerEntity, sender: PlayerEntity? ): Boolean { return current != sender }

private typealias CanSend = ( player: PlayerEntity, sender: PlayerEntity? ) -> Boolean
private typealias OnServer = ( payload: CustomPayload, context: ServerPlayNetworking.Context ) -> Unit

/** Networking server receiver registering class. */
class Receiver( private val payload: PayloadCompanion ) {

    fun register( onServer: OnServer ) {

        ServerPlayNetworking.registerGlobalReceiver( payload.id, onServer )

    }

    /**
     * Registers server receiver and broadcasts to clients after it.
     * Doesn't do any server task.
     */
    fun registerBroadcast( canSend: CanSend = ::isNotSender ) {

        register { payload, context ->

            val sender = context.player();          val server = sender.server

            serverSend(server) {

                Sender( sender, payload ).toClients( server.overworld ) {

                    player, sender -> canSend(player, sender)

                }

            }

        }

    }

    /** Register receiver on the server to broadcast. Does not send any packets.
     * Make sure if you need to, to register on both the server and client. */
    fun registerEmpty() { registerBroadcast() }

}

/** Server packet sender. */
class Sender( private val payload: CustomPayload ) {

    constructor( sender: PlayerEntity, payload: CustomPayload ) : this(payload) { this.sender = sender }

    private var sender: PlayerEntity? = null

    /** Send packets to clients. */
    fun toClients( world: World, canSend: CanSend = ::isNotSender ) {

        toClients( world.players.toSet(), canSend )

    }

    fun toClients( players: Set<PlayerEntity>, canSend: CanSend = ::isNotSender ) {

        players.forEach {

            val canSend = canSend(it, sender)

            if ( !canSend ) return@forEach;    it as ServerPlayerEntity

            ServerPlayNetworking.send( it, payload )

        }

    }

    fun toClient( player: PlayerEntity ) {

        ServerPlayNetworking.send( player as ServerPlayerEntity, payload )

    }

}

typealias Registry = PayloadTypeRegistry<RegistryByteBuf>

abstract class Payload : CustomPayload {

    val playS2C: Registry = PayloadTypeRegistry.playS2C()
    val playC2S: Registry = PayloadTypeRegistry.playC2S()

}

abstract class PayloadCompanion {

    abstract val id: CustomPayload.Id<*>
    abstract val codec: PacketCodec<ByteBuf, *>

}
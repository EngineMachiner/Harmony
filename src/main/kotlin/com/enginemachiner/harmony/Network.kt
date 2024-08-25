package com.enginemachiner.harmony

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerTask
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/** Maximum file data (bytes) that can be transferred to clients. */
const val MAX_BYTES = 32000

fun serverSend( server: MinecraftServer, runnable: Runnable ) {

    val task = ServerTask( server.ticks, runnable )

    server.send(task)

}

fun isNotSender( current: PlayerEntity, sender: PlayerEntity? ): Boolean { return current != sender }


private typealias ReadWrite = ( sent: PacketByteBuf, toSend: BufWrapper ) -> Unit
private typealias CanSend = ( player: PlayerEntity, sender: PlayerEntity? ) -> Boolean
private typealias OnServer = ( server: MinecraftServer, sender: ServerPlayerEntity, buf: PacketByteBuf ) -> Unit

abstract class AbstractReceiver( protected val id: Identifier,        private var readWrite: ReadWrite? ) {

    protected fun buf(sent: PacketByteBuf ): PacketByteBuf {

        val readWrite = readWrite ?: return PacketByteBufs.empty()


        val buf = PacketByteBufs.create()

        readWrite( sent, BufWrapper(buf) );     return buf

    }

}

/** Networking server receiver registering class. */
class Receiver( id: Identifier, readWrite: ReadWrite? = null ) : AbstractReceiver( id, readWrite ) {

    fun register( onServer: OnServer ) {

        ServerPlayNetworking.registerGlobalReceiver(id) {

            server: MinecraftServer, sender: ServerPlayerEntity,
            _: ServerPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->

            onServer( server, sender, buf )

        }

    }

    /**
     * Registers server receiver and broadcasts to clients after it.
     * Doesn't do any server task.
     */
    fun registerBroadcast( canSend: CanSend = ::isNotSender ) {

        register { server, sender, buf ->

            // Bufs needs to be read and written.

            val next = buf(buf)


            serverSend(server) {

                Sender( id, next, sender ).toClients( server.overworld ) {

                    player, sender -> canSend(player, sender)

                }

            }

        }

    }

    /** Register receiver on the server to broadcast. Does not send any packets.
     * Make sure if you need to, to register on both the server and client. */
    fun registerEmpty() { registerBroadcast() }

}

private typealias Write = (BufWrapper) -> Unit

/** Packet sender. */
abstract class AbstractSender( protected val id: Identifier,        private var write: Write? ) {

    protected var former: PacketByteBuf? = null

    protected fun buf(): PacketByteBuf {

        if ( former != null ) return former!!


        val write = write ?: return PacketByteBufs.empty()


        val buf = PacketByteBufs.create()

        write( BufWrapper(buf) );         return buf

    }

}

/** Server packet sender. */
class Sender( id: Identifier, write: Write? = null ) : AbstractSender(id, write) {

    constructor( id: Identifier, sender: PlayerEntity, write: Write? = null ) : this(id, write) {

        this.sender = sender

    }

    internal constructor( id: Identifier, former: PacketByteBuf, sender: PlayerEntity ) : this(id) {

        this.former = former;       this.sender = sender

    }

    private var sender: PlayerEntity? = null

    /** Send packets to clients. */
    fun toClients( world: World, canSend: CanSend = ::isNotSender ) {

        toClients( world.players.toSet(), canSend )

    }

    fun toClients( players: Set<PlayerEntity>, canSend: CanSend = ::isNotSender ) {

        val buf = buf() // Get it once. Not in the loop.

        players.forEach {

            val canSend = canSend(it, sender)

            if ( !canSend ) return@forEach;    it as ServerPlayerEntity

            ServerPlayNetworking.send( it, id, buf )

        }

    }

    fun toClient( player: PlayerEntity ) {

        ServerPlayNetworking.send( player as ServerPlayerEntity, id, buf() )

    }

}

class BufWrapper( val buf: PacketByteBuf ) {

    fun write(a: Boolean): BufWrapper { buf.writeBoolean(a); return this }
    fun write(a: String): BufWrapper { buf.writeString(a); return this }
    fun write(a: Float): BufWrapper { buf.writeFloat(a); return this }
    fun write(a: Int): BufWrapper { buf.writeInt(a); return this }
    fun write( a: ItemStack? ): BufWrapper { buf.writeItemStack(a); return this }
    fun write( a: BlockPos ): BufWrapper { buf.writeBlockPos(a); return this }
    fun write( a: CompoundTag ): BufWrapper { buf.writeCompoundTag(a); return this }
    fun write( a: ByteBuf ): BufWrapper { buf.writeBytes(a); return this }

}
package com.enginemachiner.harmony

import io.netty.buffer.ByteBuf
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerTask
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/** Maximum file data (bytes) that can be transferred to clients. */
const val maxData = 32000

object Network {

    @Environment(EnvType.CLIENT)
    fun hasHandler(): Boolean { return client().networkHandler != null }

}

fun serverSend( server: MinecraftServer, runnable: Runnable ) {

    val task = ServerTask( server.ticks, runnable )

    server.send(task)

}

fun isNotSender( current: PlayerEntity, sender: PlayerEntity? ): Boolean { return current != sender }


private typealias ReadWrite = ( sent: PacketByteBuf, toSend: BufWrapper ) -> Unit

private typealias OnClient = (PacketByteBuf) -> Unit
private typealias OnServer = ( server: MinecraftServer, sender: ServerPlayerEntity, buf: PacketByteBuf ) -> Unit

private typealias ShouldSend = ( player: PlayerEntity, sender: PlayerEntity? ) -> Boolean

/** Networking receiver registering class. */
class Receiver( private val id: Identifier ) {

    constructor( id: Identifier, readWrite: ReadWrite ) : this(id) {

        this.readWrite = readWrite

    }

    private var readWrite = emptyWrite

    @Environment(EnvType.CLIENT)
    fun register(onClient: OnClient) {

        ClientPlayNetworking.registerGlobalReceiver(id) {

            _: MinecraftClient, _: ClientPlayNetworkHandler,
            buf: PacketByteBuf, _: PacketSender ->

            onClient(buf)

        }

    }

    fun register(onServer: OnServer) {

        ServerPlayNetworking.registerGlobalReceiver(id) {

            server: MinecraftServer, sender: ServerPlayerEntity,
            _: ServerPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->

            onServer( server, sender, buf )

        }

    }

    private fun buf( sent: PacketByteBuf ): PacketByteBuf {

        if ( emptyWrite == readWrite ) return PacketByteBufs.empty()


        val buf = PacketByteBufs.create()

        readWrite( sent, BufWrapper(buf) );     return buf

    }

    /**
     * Registers server receiver and broadcasts to clients after it.
     * Doesn't do any server task.
     */
    fun registerBroadcast( shouldSend: ShouldSend = ::isNotSender ) {

        register { server, sender, buf ->

            // Bufs needs to be read and written.

            val next = buf(buf)


            serverSend(server) {

                Sender( id, next, sender ).toClients( server.overworld ) {

                    player, sender -> shouldSend( player, sender )

                }

            }

        }

    }

    /** Register receiver on both the server and client. Doesn't send any packets. */
    fun registerEmpty( onClient: () -> Unit ) {

        registerBroadcast();        if ( !isClient() ) return

        register { _ -> onClient() }

    }

    private companion object {

        val emptyWrite: ReadWrite = { _, _ ->  }

    }

}

private typealias Write = (BufWrapper) -> Unit

/** Packet sender. */
class Sender( private val id: Identifier,       private var write: Write ) {

    constructor( id: Identifier ) : this( id, {} ) { emptyWrite = true }

    constructor( id: Identifier, sender: PlayerEntity, write: Write ) : this( id, write ) {

        this.sender = sender

    }

    internal constructor( id: Identifier, former: PacketByteBuf, sender: PlayerEntity ) : this(id) {

        formerBuf = former;       this.sender = sender

    }

    private var emptyWrite = false
    private var formerBuf: PacketByteBuf? = null
    private var sender: PlayerEntity? = null

    private fun buf(): PacketByteBuf {

        if ( formerBuf != null ) return formerBuf!!


        if ( emptyWrite ) return PacketByteBufs.empty()


        val buf = PacketByteBufs.create()

        write( BufWrapper(buf) );         return buf

    }

    @Environment(EnvType.CLIENT)
    /** Send packets to the server. */
    fun toServer() { ClientPlayNetworking.send( id, buf() ) }

    /** Send packets to clients. */
    fun toClients( world: World, shouldSend: ShouldSend = ::isNotSender ) {

        val buf = buf() // Get it once. Not in the loop.

        world.players.forEach {

            val shouldSend = shouldSend( it, sender )

            if ( !shouldSend ) return@forEach;    it as ServerPlayerEntity

            ServerPlayNetworking.send( it, id, buf )

        }

    }

    fun toClients( players: Set<PlayerEntity>, shouldSend: ShouldSend = ::isNotSender ) {

        val buf = buf() // Get it once. Not in the loop.

        players.forEach {

            val shouldSend = shouldSend( it, sender )

            if ( !shouldSend ) return@forEach;    it as ServerPlayerEntity

            ServerPlayNetworking.send( it, id, buf )

        }

    }

    fun toClient(player: PlayerEntity) {

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
    fun write( a: NbtCompound ): BufWrapper { buf.writeNbt(a); return this }
    fun write( a: ByteBuf ): BufWrapper { buf.writeBytes(a); return this }

}
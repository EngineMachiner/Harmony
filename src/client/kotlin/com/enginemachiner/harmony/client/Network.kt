package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.AbstractReceiver
import com.enginemachiner.harmony.AbstractSender
import com.enginemachiner.harmony.BufWrapper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object Network {

    fun hasHandler(): Boolean { return client().networkHandler != null }

}

private typealias ReadWrite = ( sent: PacketByteBuf, toSend: BufWrapper ) -> Unit

private typealias OnClient = (PacketByteBuf) -> Unit

/** Networking client receiver registering class. */
class Receiver( id: Identifier, readWrite: ReadWrite? = null ) : AbstractReceiver( id, readWrite ) {

    fun register( onClient: OnClient ) {

        ClientPlayNetworking.registerGlobalReceiver(id) {

                _: MinecraftClient, _: ClientPlayNetworkHandler,
                buf: PacketByteBuf, _: PacketSender ->

            onClient(buf)

        }

    }

    /** Register receiver on the client. Does not send any packets.
     * Make sure if you need to, to register on both the server and client. */
    fun registerEmpty( onClient: () -> Unit ) { register { _ -> onClient() } }

}

private typealias Write = (BufWrapper) -> Unit

/** Client packet sender. */
class Sender( id: Identifier, write: Write? = null ) : AbstractSender(id, write) {

    /** Send packets to the server. */
    fun toServer() { ClientPlayNetworking.send( id, buf() ) }

}
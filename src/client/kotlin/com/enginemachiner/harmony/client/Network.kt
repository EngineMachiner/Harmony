package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.PayloadCompanion
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.network.packet.CustomPayload

object Network {

    fun hasHandler(): Boolean { return client().networkHandler != null }

}

private typealias OnClient = ( payload: CustomPayload, context: ClientPlayNetworking.Context ) -> Unit

/** Networking client receiver registering class. */
class Receiver( private val payload: PayloadCompanion ) {

    fun register( onClient: OnClient ) {

        ClientPlayNetworking.registerGlobalReceiver( payload.id ) {

            payload, context -> onClient(payload, context)

        }

    }

    /** Register receiver on the client. Does not send any packets.
     * Make sure if you need to, to register on both the server and client. */
    fun registerEmpty( onClient: () -> Unit ) { register { _, _ -> onClient() } }

}

/** Client packet sender. */
class Sender( private val payload: CustomPayload ) {

    /** Send packets to the server. */
    fun toServer() { ClientPlayNetworking.send(payload) }

}
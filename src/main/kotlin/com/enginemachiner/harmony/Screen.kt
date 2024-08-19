package com.enginemachiner.harmony

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload

data class CloseScreenPayload( val value: Boolean? = null ) : Payload() {

    override fun getId(): CustomPayload.Id<CloseScreenPayload> { return Companion.id }

    fun register() { playC2S.register(id, codec) }

    companion object : PayloadCompanion() {

        private val modID = modID("close_screen")
        override val id = CustomPayload.Id<CloseScreenPayload>(modID)
        override val codec: PacketCodec<ByteBuf, CloseScreenPayload> = PacketCodec.tuple( PacketCodecs.BOOL, CloseScreenPayload::value, ::CloseScreenPayload )

    }

}

data class UpdateScreenPayload( val value: Boolean? = null ) : Payload() {

    override fun getId(): CustomPayload.Id<UpdateScreenPayload> { return Companion.id }

    fun register() { playC2S.register(id, codec);     playS2C.register(id, codec) }

    companion object : PayloadCompanion() {

        private val modID = modID("update_screens")
        override val id = CustomPayload.Id<UpdateScreenPayload>(modID)
        override val codec: PacketCodec<ByteBuf, UpdateScreenPayload> = PacketCodec.tuple( PacketCodecs.BOOL, UpdateScreenPayload::value, ::UpdateScreenPayload )

    }

}

fun registerCloseScreenReceiver() {

    Receiver( CloseScreenPayload ).register { _, context ->

        val sender = context.player();              val server = sender.server

        serverSend(server) { sender.closeHandledScreen() }

    }

}

object ScreenRefresher : ModID {

    fun networking() { Receiver(UpdateScreenPayload).registerEmpty() }

}
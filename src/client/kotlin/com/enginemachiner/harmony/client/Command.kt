package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.MOD_NAME
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

typealias ClientLiteral = LiteralArgumentBuilder<FabricClientCommandSource>
typealias ClientArgument = RequiredArgumentBuilder<FabricClientCommandSource, out Any>
private typealias OnClientRegister = (dispatcher: CommandDispatcher<FabricClientCommandSource>, main: ClientLiteral ) -> Unit

object Command {

    object Client {

        private val event = ClientCommandRegistrationCallback.EVENT

        fun register( onRegister: OnClientRegister) {

            event.register { dispatcher, _ ->

                val main = ClientCommandManager.literal(MOD_NAME)

                onRegister( dispatcher, main )

            }

        }

        fun literal( s: String ): ClientLiteral { return ClientCommandManager.literal(s) }

        fun argument(type: ArgumentType<*>, name: String = "" ): ClientArgument {

            return ClientCommandManager.argument( name, type )

        }

    }

}
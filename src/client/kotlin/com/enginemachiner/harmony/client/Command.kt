package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Command
import com.enginemachiner.harmony.CommandSetup
import com.enginemachiner.harmony.HarmonyArgumentBuilder
import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

private typealias ClientCommandType = Command<FabricClientCommandSource, ClientCommand>
private typealias Builder = HarmonyArgumentBuilder<FabricClientCommandSource>
private typealias Setup = CommandSetup<ClientCommand>

/**
 * Creates and configures a client-side command.
 * @param name The name of the command
 * @param setup The setup lambda to configure the command
 * @return A configured CommandNode for registration
 */
fun clientCommand( name: String, setup: Setup ) = ClientCommand(name).apply(setup).build()

class ClientCommand( builder: Builder ) : ClientCommandType(builder) {

    constructor( name: String ) : this( literal(name) )
    constructor( name: String, type: ArgumentType<*> ) : this( argument(name, type) )

    override fun literal( name: String, setup: Setup ): ClientCommand {

        val child = ClientCommand(name).apply(setup);          builder.then( child.builder )

        return this

    }

    override fun argument( name: String, type: ArgumentType<*>, setup: Setup ): ClientCommand {

        val child = ClientCommand(name, type).apply(setup);          builder.then( child.builder )

        return this

    }

}
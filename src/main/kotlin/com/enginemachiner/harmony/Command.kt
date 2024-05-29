package com.enginemachiner.harmony

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.argument.AngleArgumentType
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

typealias Literal = LiteralArgumentBuilder<ServerCommandSource>
typealias Argument = RequiredArgumentBuilder<ServerCommandSource, out Any>
private typealias OnRegister = ( dispatcher: CommandDispatcher<ServerCommandSource>, main: Literal ) -> Unit

object Command {

    object Server {

        private val event = CommandRegistrationCallback.EVENT

        fun register( onRegister: OnRegister ) {

            event.register { dispatcher, _, _ ->

                val literal = literal(MOD_NAME)

                onRegister( dispatcher, literal )

            }

        }

        fun literal( s: String ): Literal { return CommandManager.literal(s) }

        fun argument( type: ArgumentType<*>, name: String = "" ): Argument {

            return CommandManager.argument( name, type )

        }

    }

    object Arguments {

        fun bool(): BoolArgumentType { return BoolArgumentType.bool() }

        fun bool( ctx: CommandContext<*>, name: String = "" ): Boolean {

            return BoolArgumentType.getBool( ctx, name )

        }


        fun string(): StringArgumentType { return StringArgumentType.string() }

        fun string( ctx: CommandContext<*>, name: String = "" ): String {

            return StringArgumentType.getString( ctx, name )

        }


        fun int(): IntegerArgumentType { return IntegerArgumentType.integer() }

        fun int( ctx: CommandContext<*>, name: String = "" ): Int {

            return IntegerArgumentType.getInteger( ctx, name )

        }


        fun float(): FloatArgumentType { return FloatArgumentType.floatArg() }

        fun float( ctx: CommandContext<*>, name: String = "" ): Float {

            return FloatArgumentType.getFloat( ctx, name )

        }


        fun double(): DoubleArgumentType { return DoubleArgumentType.doubleArg() }

        fun double( ctx: CommandContext<*>, name: String = "" ): Double {

            return DoubleArgumentType.getDouble( ctx, name )

        }


        fun color(): ColorArgumentType { return ColorArgumentType.color() }

        fun color( ctx: CommandContext<ServerCommandSource>, name: String = "" ): Formatting {

            return ColorArgumentType.getColor( ctx, name )

        }


        fun blockPos(): BlockPosArgumentType { return BlockPosArgumentType.blockPos() }

        fun blockPos( ctx: CommandContext<ServerCommandSource>, name: String = "" ): BlockPos {

            return BlockPosArgumentType.getBlockPos( ctx, name )

        }


        fun angle(): AngleArgumentType { return AngleArgumentType.angle() }

        fun angle( ctx: CommandContext<ServerCommandSource>, name: String = "" ): Float {

            return AngleArgumentType.getAngle( ctx, name )

        }

    }

}
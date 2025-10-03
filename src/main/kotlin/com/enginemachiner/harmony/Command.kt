package com.enginemachiner.harmony

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg
import com.mojang.brigadier.arguments.FloatArgumentType.floatArg
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.argument.*
import net.minecraft.entity.Entity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

typealias HarmonyArgumentBuilder<T> = ArgumentBuilder<T, *>
typealias CommandSetup<V> = V.() -> Unit

private typealias Builder<T> = HarmonyArgumentBuilder<T>
private typealias Setup<V> = CommandSetup<V>

abstract class Command<T, V>( val builder: Builder<T> ) {

    fun build(): CommandNode<T> = builder.build()

    fun executes( action: CommandContext<T>.() -> Int ): Command<T, V> {

        builder.executes { action(it) };            return this

    }

    fun requires( predicate: T.() -> Boolean ): Command<T, V> {

        builder.requires { predicate(it) };            return this

    }

    abstract fun literal( name: String, setup: Setup<V> ): Command<T, V>
    abstract fun argument( name: String, type: ArgumentType<*>, setup: Setup<V> ): Command<T, V>

    fun bool( name: String = "bool", setup: Setup<V> ) = argument( name, BOOL, setup )
    fun string( name: String = "message", setup: Setup<V> ) = argument( name, STRING, setup )

    fun int( name: String = "integer", setup: Setup<V> ) = argument( name, integer(), setup )
    fun int( name: String = "integer", min: Int, max: Int, setup: Setup<V> ) = argument( name, integer(min, max), setup )

    fun float( name: String = "float", setup: Setup<V> ) = argument( name, floatArg(), setup )
    fun float( name: String = "float", min: Float, max: Float, setup: Setup<V> ) = argument( name, floatArg(min, max), setup )

    fun double( name: String = "double", setup: Setup<V> ) = argument( name, doubleArg(), setup )
    fun double( name: String = "double", min: Double, max: Double, setup: Setup<V> ) = argument( name, doubleArg(min, max), setup )

    protected companion object {

        val BOOL: BoolArgumentType = BoolArgumentType.bool()
        val STRING: StringArgumentType = StringArgumentType.string()

    }

}

private typealias ServerCommandType = Command<ServerCommandSource, ServerCommand>
private typealias ServerBuilder = Builder<ServerCommandSource>
private typealias ServerSetup = Setup<ServerCommand>

fun serverCommand( name: String, setup: ServerSetup ) = ServerCommand(name).apply(setup).build()

class ServerCommand( builder: ServerBuilder ) : ServerCommandType(builder) {

    constructor( name: String ) : this( literal(name) )
    constructor( name: String, type: ArgumentType<*> ) : this( argument(name, type) )

    override fun literal( name: String, setup: ServerSetup ): ServerCommand {

        val child = ServerCommand(name).apply(setup);           builder.then( child.builder )

        return this

    }

    override fun argument( name: String, type: ArgumentType<*>, setup: ServerSetup ): ServerCommand {

        val child = ServerCommand(name, type).apply(setup);          builder.then( child.builder )

        return this

    }

    fun color( name: String = "color", setup: ServerSetup ) = argument( name, COLOR, setup )
    fun angle( name: String = "angle", setup: ServerSetup ) = argument( name, ANGLE, setup )
    fun rotation( name: String = "rotation", setup: ServerSetup ) = argument( name, ROTATION, setup )

    fun blockPos( name: String = "pos", setup: ServerSetup ) = argument( name, BLOCK_POS, setup )
    fun vec3( name: String = "x y z", setup: ServerSetup ) = argument( name, VEC3, setup )
    fun vec2( name: String = "x y", setup: ServerSetup ) = argument( name, VEC2, setup )

    fun player( name: String = "player", setup: ServerSetup ) = argument( name, ENTITY, setup )
    fun entity( name: String = "entity", setup: ServerSetup ) = argument( name, ENTITY, setup )
    fun entities( name: String = "entities", setup: ServerSetup ) = argument( name, ENTITIES, setup )

    private companion object {

        val COLOR: ColorArgumentType = ColorArgumentType.color()
        val ANGLE: AngleArgumentType = AngleArgumentType.angle()
        val ROTATION: RotationArgumentType = RotationArgumentType.rotation()

        val BLOCK_POS: BlockPosArgumentType = BlockPosArgumentType.blockPos()
        val VEC3: Vec3ArgumentType = Vec3ArgumentType.vec3()
        val VEC2: Vec2ArgumentType = Vec2ArgumentType.vec2()

        val ENTITY: EntityArgumentType = EntityArgumentType.entity()
        val ENTITIES: EntityArgumentType = EntityArgumentType.entities()

    }

}

// Extension functions for easier argument retrieval.

fun CommandContext<*>.bool( name: String = "bool" ): Boolean = BoolArgumentType.getBool(this, name)
fun CommandContext<*>.int( name: String = "integer" ): Int = IntegerArgumentType.getInteger(this, name)
fun CommandContext<*>.float( name: String = "float" ): Float = FloatArgumentType.getFloat(this, name)
fun CommandContext<*>.double( name: String = "double" ): Double = DoubleArgumentType.getDouble(this, name)
fun CommandContext<*>.string( name: String = "message" ): String = StringArgumentType.getString(this, name)

fun CommandContext<ServerCommandSource>.color( name: String = "color" ): Formatting = ColorArgumentType.getColor(this, name)
fun CommandContext<ServerCommandSource>.angle( name: String = "angle" ): Float = AngleArgumentType.getAngle(this, name)
fun CommandContext<ServerCommandSource>.rotation( name: String = "rotation" ): PosArgument = RotationArgumentType.getRotation(this, name)

fun CommandContext<ServerCommandSource>.blockPos( name: String = "pos" ): BlockPos = BlockPosArgumentType.getBlockPos(this, name)
fun CommandContext<ServerCommandSource>.vec3( name: String = "x y z" ): Vec3d = Vec3ArgumentType.getVec3(this, name)
fun CommandContext<ServerCommandSource>.vec2( name: String = "x y" ): Vec2f = Vec2ArgumentType.getVec2(this, name)

fun CommandContext<ServerCommandSource>.player( name: String = "player" ): ServerPlayerEntity = EntityArgumentType.getPlayer(this, name)
fun CommandContext<ServerCommandSource>.entity( name: String = "entity" ): Entity = EntityArgumentType.getEntity(this, name)
fun CommandContext<ServerCommandSource>.entities( name: String = "entities" ): Collection<Entity> = EntityArgumentType.getEntities(this, name)

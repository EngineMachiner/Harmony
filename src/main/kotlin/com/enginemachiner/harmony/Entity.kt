package com.enginemachiner.harmony

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockWithEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion.DestructionType

@Environment(EnvType.CLIENT)
fun world(): ClientWorld? { return client().world }

/** Searches a client world entity by its id. Mostly used on networking. */
@Environment(EnvType.CLIENT)
fun entity(id: Int): Entity? { return world()!!.getEntityById(id) }

@Environment(EnvType.CLIENT)
fun player(): ClientPlayerEntity { return client().player!! }

fun addVelocity( entity: Entity, delta: Vec3d ) { entity.addVelocity( delta.x, delta.y, delta.z ) }

fun explode( e: Entity, power: Float, type: DestructionType, createFire: Boolean = false ) {

    val world = e.world;    world.createExplosion( e, e.x, e.y, e.z, power, createFire, type )

}

abstract class BlockWithEntity(settings: Settings) : BlockWithEntity(settings), ModID

package com.enginemachiner.harmony

import net.minecraft.block.BlockWithEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion.DestructionType

fun addVelocity( entity: Entity, delta: Vec3d ) { entity.addVelocity( delta.x, delta.y, delta.z ) }

fun explode( e: Entity, power: Float, type: DestructionType, createFire: Boolean = false ) {

    val world = e.world;        world.createExplosion( e, e.x, e.y, e.z, power, createFire, type )

}

abstract class BlockWithEntity(settings: Settings) : BlockWithEntity(settings), ModID

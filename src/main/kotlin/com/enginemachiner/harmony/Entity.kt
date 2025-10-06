package com.enginemachiner.harmony

import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion.DestructionType

/** Converts Hand to its corresponding EquipmentSlot. */
fun Hand.toEquipmentSlot(): EquipmentSlot {

    return when (this) {
        Hand.MAIN_HAND -> EquipmentSlot.MAINHAND
        Hand.OFF_HAND -> EquipmentSlot.OFFHAND
    }

}

/**
 * Adds velocity to this entity.
 *
 * @param delta The velocity vector to add.
 */
fun Entity.addVelocity( delta: Vec3d ) { addVelocity( delta.x, delta.y, delta.z ) }

/** Creates an explosion at this entity's position. */
fun Entity.explode( power: Float, type: DestructionType, createFire: Boolean = false ) {

    world.createExplosion( this, x, y, z, power, createFire, type )

}
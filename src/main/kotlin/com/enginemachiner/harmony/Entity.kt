package com.enginemachiner.harmony

import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion.DestructionType

/**
 * Converts a Hand enum value to its corresponding EquipmentSlot enum value.
 */
fun Hand.toEquipmentSlot(): EquipmentSlot {

    return when (this) {
        Hand.MAIN_HAND -> EquipmentSlot.MAINHAND
        Hand.OFF_HAND -> EquipmentSlot.OFFHAND
    }

}

fun Entity.addVelocity( delta: Vec3d ) { addVelocity( delta.x, delta.y, delta.z ) }

fun Entity.explode( power: Float, type: DestructionType, createFire: Boolean = false ) {

    world.createExplosion( this, x, y, z, power, createFire, type )

}
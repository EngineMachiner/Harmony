package com.enginemachiner.harmony

import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

object Particles {

    const val MIN_DISTANCE = 36.0

    fun register( path: String, particle: DefaultParticleType ) {
        Registry.register( Registries.PARTICLE_TYPE, modID(path), particle )
    }

    fun spawn( world: ServerWorld, particle: ParticleEffect, pos: Vec3d, count: Int = 1, delta: Vec3d = Vec3d.ZERO, speed: Double = 0.0 ) {
        world.spawnParticles( particle, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed )
    }

}
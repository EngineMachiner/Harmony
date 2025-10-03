package com.enginemachiner.harmony

import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3d.ZERO

fun ServerWorld.spawnParticles(
    particle: ParticleEffect,
    pos: Vec3d,
    count: Int = 1,
    delta: Vec3d = ZERO,
    speed: Double = 0.0
) { spawnParticles( particle, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed ) }

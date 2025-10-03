package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.randomColor
import net.minecraft.client.particle.*
import net.minecraft.client.particle.ParticleTextureSheet.PARTICLE_SHEET_OPAQUE
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3d.ZERO

fun ParticleManager.addParticle( particle: ParticleEffect, pos: Vec3d, delta: Vec3d = ZERO ): Particle? {

    return addParticle( particle, pos.x, pos.y, pos.z, delta.x, delta.y, delta.z )

}

open class HarmonyParticle( world: ClientWorld, pos: Vec3d ) : SpriteBillboardParticle( world, pos.x, pos.y, pos.z ) {

    fun setRandomColor() {

        val color = randomColor().getColorComponents(null);             setColor( color[0], color[1], color[2] )

    }

    override fun getType(): ParticleTextureSheet = PARTICLE_SHEET_OPAQUE

    companion object {

        abstract class Factory( private val provider: SpriteProvider ) : ParticleFactory<DefaultParticleType> {

            open fun template( world: ClientWorld, pos: Vec3d ) = HarmonyParticle( world, pos )

            override fun createParticle(

                parameters: DefaultParticleType,    world: ClientWorld,         x: Double, y: Double, z: Double,

                velocityX: Double, velocityY: Double, velocityZ: Double

            ): Particle {

                val pos = Vec3d(x, y, z);           val particle = template(world, pos)

                particle.setSprite(provider);           return particle

            }

        }

    }

}
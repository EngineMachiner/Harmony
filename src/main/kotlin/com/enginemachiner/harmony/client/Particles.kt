package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.randomColor
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.math.Vec3d

object Particles {

    fun spawnOne( particle: ParticleEffect, pos: Vec3d, delta: Vec3d = Vec3d.ZERO ): Particle? {

        world() ?: return null;         val manager = client().particleManager

        return manager.addParticle( particle, pos.x, pos.y, pos.z, delta.x, delta.y, delta.z )

    }

}

open class SimpleParticle( clientWorld: ClientWorld, x: Double, y: Double, z: Double ) : SpriteBillboardParticle( clientWorld, x, y, z ) {

    init { wrap() };     private fun wrap() { init() };    open fun init() {}

    fun setRandomColor() {

        val color = randomColor().getColorComponents(null)

        setColor( color[0], color[1], color[2] )

    }

    override fun getType(): ParticleTextureSheet { return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE }

    companion object {

        @Environment(EnvType.CLIENT)
        abstract class Factory( private var provider: SpriteProvider ) : ParticleFactory<DefaultParticleType> {

            open fun template( world: ClientWorld, x: Double, y: Double, z: Double ): SpriteBillboardParticle {
                return SimpleParticle(world, x, y, z)
            }

            override fun createParticle(

                parameters: DefaultParticleType, world: ClientWorld,

                x: Double, y: Double, z: Double,

                velocityX: Double, velocityY: Double, velocityZ: Double

            ): Particle {

                val particle = template(world, x, y, z);        particle.setSprite(provider)

                return particle

            }

        }

    }

}
package com.enginemachiner.harmony.client

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity

fun world(): ClientWorld? { return client().world }

/** Searches a client world entity by its id. Mostly used on networking. */
fun entity(id: Int): Entity? { return world()!!.getEntityById(id) }

fun player(): ClientPlayerEntity { return client().player!! }
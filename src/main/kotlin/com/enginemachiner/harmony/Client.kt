package com.enginemachiner.harmony

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
fun client(): MinecraftClient { return MinecraftClient.getInstance()!! }

/** Checks if the current environment is on the client. If you have split sources, you might not need to use this at all. */
fun isClient(): Boolean { return FabricLoaderImpl.INSTANCE.environmentType == EnvType.CLIENT }

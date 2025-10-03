package com.enginemachiner.harmony.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

fun client() = MinecraftClient.getInstance()!!

fun textRenderer(): TextRenderer = client().textRenderer


fun world() = client().world

fun player() = client().player!!


fun currentScreen() = client().currentScreen

fun MinecraftClient.isOnScreen() = currentScreen() != null


fun inventory(): PlayerInventory = player().inventory

fun stack( slot: Int ): ItemStack = inventory().getStack(slot)
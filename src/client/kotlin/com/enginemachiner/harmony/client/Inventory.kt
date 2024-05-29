package com.enginemachiner.harmony.client

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

fun inventory(): PlayerInventory { return player().inventory }

fun stack(slot: Int): ItemStack { return inventory().getStack(slot) }
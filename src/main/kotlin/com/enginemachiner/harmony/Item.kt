package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.item.ItemGroup
import net.minecraft.item.ToolItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper.nextFloat
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry.ITEM
import java.awt.Color

fun simpleItemGroup( id: Identifier,    item: Item = defaultItem ): ItemGroup {

    val stack = item.defaultStack

    return FabricItemGroupBuilder.create(id).icon { stack }.build()

}

private val defaultItem = Item( Settings() )

internal fun Mod.itemGroup(): ItemGroup {

    val id = identifiers.itemGroup;         return simpleItemGroup(id)

}

fun Mod.itemSettings( maxCount: Int = 1 ): Settings {

    return Settings().group(itemGroup).maxCount(maxCount)

}

/** Checks if the item is from a Harmony mod. */
fun Item.isFrom( mod: Mod ): Boolean {

    val id = ITEM.getId(this);          return id.namespace == mod.name

}

/** Checks if the stack item is from a Harmony mod. */
fun ItemStack.isFrom( mod: Mod ) = item.isFrom(mod)

fun ItemStack.damage( entity: LivingEntity, hand: Hand, damage: Int = 1 ) {

    val equipmentSlot = hand.toEquipmentSlot()

    damage( damage, entity ) { entity.sendEquipmentBreakStatus(equipmentSlot) }

}

/**
 * Interface to create a NBT compound under the mod's name
 * to prevent conflicts between different mods.
 */
interface ModItem {

    val mod: Mod

    fun ItemStack.init(): ItemStack {

        val nbt = NbtCompound();            val name = mod.name

        orCreateNbt.put( name, nbt );           return this

    }

    fun ItemStack.modNBT(): NbtCompound {

        val name = mod.name;            return nbt!!.getCompound(name)

    }

    open fun onInventoryChange( oldStack: ItemStack, newStack: ItemStack ) {}

}

abstract class Item( settings: Settings ) : Item(settings), ModItem {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ) = false

    override fun getDefaultStack() = super.getDefaultStack().init()

}

abstract class ToolItem( material: ToolMaterial, settings: Settings ) : ToolItem( material, settings ), ModItem {

    override fun allowNbtUpdateAnimation( player: PlayerEntity, hand: Hand, oldStack: ItemStack, newStack: ItemStack ) = false

    override fun getDefaultStack() = super.getDefaultStack().init()

}

/** An interface that provides color handling capabilities for items. */
interface ColorItem : ModItem {

    /** Returns the color to be set in the NBT. */
    fun color(): Color {

        val saturation = 0.35f;             val brightness = randomBrightness()

        return randomColor( saturation, brightness )

    }

    /** Returns the color stored in the mod NBT. */
    fun color( stack: ItemStack ) = stack.modNBT().getInt("color")

    /** Sets the color. This function is to be used when overriding getDefaultStack(). */
    fun ItemStack.setColor(): ItemStack {

        val color = color().rgb;        modNBT().putInt( "color", color );       return this

    }

    private companion object {

        fun randomBrightness(): Float {

            val random = Random.create();           return nextFloat( random, 0.75f, 1f )

        }

    }

}
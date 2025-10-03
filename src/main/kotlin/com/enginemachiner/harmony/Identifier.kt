package com.enginemachiner.harmony

import net.minecraft.util.Identifier

fun Mod.id( path: String ) = Identifier( id, path )

fun Mod.textureID( path: String ) = id( "textures/$path" )

class Identifiers( mod: Mod ) {

    val itemGroup = mod.id("item_group")

    companion object {

        val UPDATE_STACK_NBT = Identifier( "harmony", "update_stack_nbt" )

    }

}
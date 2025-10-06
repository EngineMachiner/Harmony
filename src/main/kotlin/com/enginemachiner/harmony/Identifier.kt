package com.enginemachiner.harmony

import net.minecraft.util.Identifier

/**
 * Creates an Identifier for this mod with the given path.
 *
 * @param path The resource path.
 * @return An Identifier in the format "modid:path".
 */
fun Mod.id( path: String ) = Identifier( id, path )

/**
 * Creates a texture Identifier for this mod.
 *
 * @param path The texture path (automatically prefixed with "textures/").
 * @return An Identifier in the format "modid:textures/path".
 */
fun Mod.textureID( path: String ) = id( "textures/$path" )

/** Common identifiers used by a mod. */
class Identifiers( mod: Mod ) {

    val itemGroup = mod.id("item_group")

    companion object {

        /** Identifier for the update stack NBT packet. */
        val UPDATE_STACK_NBT = Identifier( "harmony", "update_stack_nbt" )

    }

}
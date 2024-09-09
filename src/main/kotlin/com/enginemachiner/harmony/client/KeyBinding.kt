package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.MOD_NAME
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

/** It registers the mod keybindings. */
class ModKey( key: String, category: String, type: InputUtil.Type = InputUtil.Type.KEYSYM, glfwCode: Int = GLFW.GLFW_KEY_UNKNOWN ) {

    fun bind(): KeyBinding { return bind }

    private val bind = KeyBinding( KEY + key, type, glfwCode, CATEGORY + category )

    fun register() { KeyBindingHelper.registerKeyBinding(bind) }

    private companion object {

        val KEY = "key.$MOD_NAME.";         val CATEGORY = "category.$MOD_NAME."

    }

}
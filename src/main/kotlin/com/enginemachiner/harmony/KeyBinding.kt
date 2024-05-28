package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class ModKey( key: String, category: String, type: InputUtil.Type = InputUtil.Type.KEYSYM, glfwCode: Int = GLFW.GLFW_KEY_UNKNOWN ) {

    fun bind(): KeyBinding { return bind }

    private val bind = KeyBinding( KEY + key, type, glfwCode, CATEGORY + category )

    fun register() { KeyBindingHelper.registerKeyBinding(bind) }

    private companion object {

        val KEY = "key.$MOD_NAME.";         val CATEGORY = "category.$MOD_NAME."

    }

}
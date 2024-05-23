package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

class ModKeyBind( key: String, category: String, type: InputUtil.Type = InputUtil.Type.KEYSYM, glfwCode: Int = GLFW.GLFW_KEY_UNKNOWN ) {

    private val keyBind = KeyBinding( KEY + key, type, glfwCode, CATEGORY + category )

    fun it(): KeyBinding { return keyBind }

    fun register() { KeyBindingHelper.registerKeyBinding(keyBind) }

    private companion object {

        val KEY = "key.$MOD_NAME.";         val CATEGORY = "category.$MOD_NAME."

    }

}
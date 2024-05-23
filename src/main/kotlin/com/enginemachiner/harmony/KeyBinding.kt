package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeyBinding {

    private val CATEGORY = "category.$MOD_NAME"

    private fun register( key: String, type: InputUtil.Type = InputUtil.Type.KEYSYM, glfwCode: Int = GLFW.GLFW_KEY_UNKNOWN ): KeyBinding {

        val preKey = "key.$MOD_NAME."

        val keyBind = KeyBinding( preKey + key, type, glfwCode, CATEGORY )

        return KeyBindingHelper.registerKeyBinding(keyBind)

    }

}
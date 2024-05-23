package com.enginemachiner.harmony

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object ModKeyBindings {

    private val CATEGORY = "category.$MOD_NAME."

    fun register( key: String, category: String, type: InputUtil.Type = InputUtil.Type.KEYSYM, glfwCode: Int = GLFW.GLFW_KEY_UNKNOWN ): KeyBinding {

        val preKey = "key.$MOD_NAME."

        val keyBind = KeyBinding( preKey + key, type, glfwCode, CATEGORY + category )

        return KeyBindingHelper.registerKeyBinding(keyBind)

    }

}
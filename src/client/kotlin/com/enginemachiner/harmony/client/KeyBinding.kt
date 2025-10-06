package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Mod
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.InputUtil.Type.KEYSYM
import org.lwjgl.glfw.GLFW

class KeybindsManager( mod: Mod ) {

    private val id = mod.id

    /**
     * Creates and configures a key binding.
     * @param subKey The key identifier suffix
     * @param subCategory The category identifier suffix
     * @param code The default key code (default: UNKNOWN)
     * @param type The input type (default: KEYSYM)
     */
    inner class Binder( subKey: String, subCategory: String, code: Int = Keys.UNKNOWN, type: InputUtil.Type = KEYSYM ) {

        val key = "key.$id.$subKey";             val category = "category.$id.$subCategory"

        val keyBinding = KeyBinding( key, type, code, category )

        fun register() { KeyBindingHelper.registerKeyBinding( keyBinding ) }

    }

}

/** Key code constants for common keyboard keys. */
object Keys {

    const val F1 = GLFW.GLFW_KEY_F1
    const val F2 = GLFW.GLFW_KEY_F2
    const val F3 = GLFW.GLFW_KEY_F3
    const val F4 = GLFW.GLFW_KEY_F4
    const val F5 = GLFW.GLFW_KEY_F5
    const val F6 = GLFW.GLFW_KEY_F6
    const val F7 = GLFW.GLFW_KEY_F7
    const val F8 = GLFW.GLFW_KEY_F8
    const val F9 = GLFW.GLFW_KEY_F9
    const val F10 = GLFW.GLFW_KEY_F10
    const val F11 = GLFW.GLFW_KEY_F11
    const val F12 = GLFW.GLFW_KEY_F12

    const val A = GLFW.GLFW_KEY_A
    const val B = GLFW.GLFW_KEY_B
    const val C = GLFW.GLFW_KEY_C
    const val D = GLFW.GLFW_KEY_D
    const val E = GLFW.GLFW_KEY_E
    const val F = GLFW.GLFW_KEY_F
    const val G = GLFW.GLFW_KEY_G
    const val H = GLFW.GLFW_KEY_H
    const val I = GLFW.GLFW_KEY_I
    const val J = GLFW.GLFW_KEY_J
    const val K = GLFW.GLFW_KEY_K
    const val L = GLFW.GLFW_KEY_L
    const val M = GLFW.GLFW_KEY_M
    const val N = GLFW.GLFW_KEY_N
    const val O = GLFW.GLFW_KEY_O
    const val P = GLFW.GLFW_KEY_P
    const val Q = GLFW.GLFW_KEY_Q
    const val R = GLFW.GLFW_KEY_R
    const val S = GLFW.GLFW_KEY_S
    const val T = GLFW.GLFW_KEY_T
    const val U = GLFW.GLFW_KEY_U
    const val V = GLFW.GLFW_KEY_V
    const val W = GLFW.GLFW_KEY_W
    const val X = GLFW.GLFW_KEY_X
    const val Y = GLFW.GLFW_KEY_Y
    const val Z = GLFW.GLFW_KEY_Z

    const val NUM_0 = GLFW.GLFW_KEY_0
    const val NUM_1 = GLFW.GLFW_KEY_1
    const val NUM_2 = GLFW.GLFW_KEY_2
    const val NUM_3 = GLFW.GLFW_KEY_3
    const val NUM_4 = GLFW.GLFW_KEY_4
    const val NUM_5 = GLFW.GLFW_KEY_5
    const val NUM_6 = GLFW.GLFW_KEY_6
    const val NUM_7 = GLFW.GLFW_KEY_7
    const val NUM_8 = GLFW.GLFW_KEY_8
    const val NUM_9 = GLFW.GLFW_KEY_9

    const val KP_0 = GLFW.GLFW_KEY_KP_0
    const val KP_1 = GLFW.GLFW_KEY_KP_1
    const val KP_2 = GLFW.GLFW_KEY_KP_2
    const val KP_3 = GLFW.GLFW_KEY_KP_3
    const val KP_4 = GLFW.GLFW_KEY_KP_4
    const val KP_5 = GLFW.GLFW_KEY_KP_5
    const val KP_6 = GLFW.GLFW_KEY_KP_6
    const val KP_7 = GLFW.GLFW_KEY_KP_7
    const val KP_8 = GLFW.GLFW_KEY_KP_8
    const val KP_9 = GLFW.GLFW_KEY_KP_9

    const val SPACE = GLFW.GLFW_KEY_SPACE
    const val ENTER = GLFW.GLFW_KEY_ENTER
    const val TAB = GLFW.GLFW_KEY_TAB
    const val ESCAPE = GLFW.GLFW_KEY_ESCAPE
    const val BACKSPACE = GLFW.GLFW_KEY_BACKSPACE
    const val DELETE = GLFW.GLFW_KEY_DELETE
    const val INSERT = GLFW.GLFW_KEY_INSERT
    const val HOME = GLFW.GLFW_KEY_HOME
    const val END = GLFW.GLFW_KEY_END
    const val PAGE_UP = GLFW.GLFW_KEY_PAGE_UP
    const val PAGE_DOWN = GLFW.GLFW_KEY_PAGE_DOWN

    const val UP = GLFW.GLFW_KEY_UP
    const val DOWN = GLFW.GLFW_KEY_DOWN
    const val LEFT = GLFW.GLFW_KEY_LEFT
    const val RIGHT = GLFW.GLFW_KEY_RIGHT

    const val LEFT_SHIFT = GLFW.GLFW_KEY_LEFT_SHIFT
    const val RIGHT_SHIFT = GLFW.GLFW_KEY_RIGHT_SHIFT
    const val LEFT_CONTROL = GLFW.GLFW_KEY_LEFT_CONTROL
    const val RIGHT_CONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL
    const val LEFT_ALT = GLFW.GLFW_KEY_LEFT_ALT
    const val RIGHT_ALT = GLFW.GLFW_KEY_RIGHT_ALT

    const val UNKNOWN = GLFW.GLFW_KEY_UNKNOWN

}
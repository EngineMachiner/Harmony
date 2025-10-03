package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.Mod

class ClientMod( mod: Mod ) {

    val keybindsManager = KeybindsManager(mod)
    val networkingManager = ClientNetworking(mod)

}
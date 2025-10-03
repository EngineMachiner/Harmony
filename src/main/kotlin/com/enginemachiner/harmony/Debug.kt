package com.enginemachiner.harmony

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

class Debug( mod: Mod ) {

    private val name = mod.name;            val logger: Logger = getLogger(name)

    fun print( a: Any? ) { logger.info("$a") }

    @JvmField // For Java.
    val print: (String) -> Unit = ::print

}
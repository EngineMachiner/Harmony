package com.enginemachiner.harmony

import java.util.logging.Logger

/** Verify logic or methods if they are based on vanilla behavior, etc. */
annotation class BasedOn( val reason: String )

private val logger = Logger.getLogger(MOD_TITLE)

fun modPrint( a: Any? ) { logger.info("$a") }

@JvmField   /** Meant to be used on Java. */
val modPrint: (String) -> Unit = ::modPrint
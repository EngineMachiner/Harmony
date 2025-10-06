package com.enginemachiner.harmony

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

/** Provides debugging and logging utilities for a mod. */
class Debug( mod: Mod ) {

    private val name = mod.name
    
    /** The SLF4J logger instance for this mod. */
    val logger: Logger = getLogger(name)

    /**
     * Prints a message to the logger at INFO level.
     *
     * @param any The object to print. Will be converted to string.
     */
    fun print( any: Any? ) { logger.info("$any") }

    /**
     * Function reference to [print] for Java interoperability.
     * Allows Java code to use method references.
     */
    @JvmField
    val print: (String) -> Unit = ::print

}
package com.enginemachiner.harmony

import kotlinx.coroutines.*

/** A coroutine runner that provides a scoped and named context for launching coroutines. */
class Runner( mod: Mod, var name: String ) {

    private val modName = mod.name;             val scope = CoroutineScope( Dispatchers.IO )

    private fun coroutineName() = CoroutineName("$modName $name")

    /** Launches a new coroutine that computes a result asynchronously. */
    fun <T> async( block: suspend () -> T ): Deferred<T> {

        val name = coroutineName();          return scope.async(name) { block() }

    }

    /** Launches a new coroutine that doesn't return a result. */
    fun launch( block: suspend () -> Unit ): Job {

        val name = coroutineName();          return scope.launch(name) { block() }

    }

    /**
     * Cancels all coroutines started by this Runner. This should be called
     * when the Runner is no longer needed to prevent memory leaks.
     */
    fun cancel() { scope.cancel() }

}

fun Mod.runner( name: String ) = Runner(this, name)
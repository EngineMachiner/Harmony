package com.enginemachiner.harmony

import com.enginemachiner.harmony.Async.thread
import kotlinx.coroutines.*

object Async {

    fun thread(): Thread { return Thread.currentThread() }

    fun threadID(): Long { return thread().id }

}

class Coroutine( var name: String? = null ) {

    val SCOPE = CoroutineScope( Dispatchers.IO )

    private fun setName() {

        var name = name ?: return

        val contains = name.contains("thread")

        if ( !contains ) name += " thread"

        thread().name = "$MOD_TITLE $name"

    }

    fun async( block: () -> Unit ): Deferred<Unit> {

        return SCOPE.async { setName();  block() }

    }

    fun launch( block: () -> Unit ): Job {

        return SCOPE.launch { setName();  block() }

    }

}
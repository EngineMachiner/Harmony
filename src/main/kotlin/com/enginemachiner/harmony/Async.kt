package com.enginemachiner.harmony

import kotlinx.coroutines.*

object Async {

    fun thread(): Thread { return Thread.currentThread() }

    fun threadID(): Long { return thread().threadId() }


    val scope = CoroutineScope( Dispatchers.IO ) as Scope

    interface Scope : CoroutineScope {

        private fun setThreadName( name: String? ) {

            var name = name ?: return

            val contains = name.contains("thread")

            if ( !contains ) name += " thread"

            thread().name = "$MOD_TITLE $name"

        }

        fun async( threadName: String, block: () -> Unit ): Deferred<Unit> {

            return async { setThreadName(threadName);  block() }

        }

        fun launch( threadName: String, block: () -> Unit ) {

            launch { setThreadName(threadName);  block() }

        }

    }

}
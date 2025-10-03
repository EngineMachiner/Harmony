package com.enginemachiner.harmony

private fun currentThreadID() = Thread.currentThread().id

class Timer( private val tickLimit: Int,     private val callback: () -> Unit ) {

    init { timers.add(this) };          private val threadID = currentThreadID()

    private var ticks = 0;          private var remove = false

    fun remove() { remove = true }

    internal fun tick() {

        if ( threadID != currentThreadID() || remove ) return

        if ( ticks > tickLimit ) { callback(); remove() } else ticks++

    }

    companion object {

        val timers = mutableListOf<Timer>()

        fun tickTimers() { timers.forEach { it.tick() };           timers.removeIf { it.remove } }

    }

}
package com.enginemachiner.harmony

private fun currentThreadID() = Thread.currentThread().id

/**
 * Timer class that executes a callback after a specified number of ticks.
 * The timer must be ticked manually using the companion object's tickTimers() method.
 *
 * @property tickLimit The number of ticks to wait before executing the callback
 * @property callback The function to execute when the timer completes
 */
class Timer( private val tickLimit: Int,     private val callback: () -> Unit ) {

    init { timers.add(this) };          private val threadID = currentThreadID()

    private var ticks = 0;          private var remove = false

    /** Marks this timer for removal on the next tick. */
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
package com.enginemachiner.harmony

private fun threadID(): Long { return Thread.currentThread().id }

class Timer( private val tickLimit: Int,     private val function: () -> Unit ) {

    private val id = threadID();        var remove = false

    private var ticks = 0;      init { timers.add(this) }

    private fun kill() { remove = true }

    fun tick() {

        if ( id != threadID() || remove ) { return }

        if ( ticks > tickLimit ) { function(); kill() } else ticks++

    }

    companion object {

        val timers = mutableListOf<Timer?>()

        fun tickTimers() {

            val list = timers.toList().filterNotNull();      list.forEach { it.tick() }

            list.forEach { if ( it.remove ) timers.remove(it) }

        }

    }

}
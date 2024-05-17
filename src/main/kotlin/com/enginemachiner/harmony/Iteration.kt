package com.enginemachiner.harmony

/** Gets value keeping in size when index is out of bounds. */
fun <T: Any> cycle( collection: Collection<T>, value: T, i: Int = 1 ): T {

        val size = collection.size;         val i = collection.indexOf(value) + 1

        return collection.elementAt( i % size )

}
package com.enginemachiner.harmony

/**
 * Returns the element that is [offset] positions away from the specified [value] in the collection.
 * If the resulting position is out of bounds, it wraps around to the beginning or end of the collection.
 *
 * @param value The element to start from
 * @param offset The number of positions to move (positive or negative)
 * @return The element at the calculated position
 * @throws NoSuchElementException if the collection is empty or the collection doesn't contain the specified [value]
 *
 */
fun <T> Collection<T>.cycle( value: T, offset: Int = 1 ): T {

    val i = indexOf(value) + offset;             return elementAt( i % size )

}
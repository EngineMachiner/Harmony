package com.enginemachiner.harmony

/** Shortens string and adds "..." by a number of chars. */
fun shorten( s: String, limit: Float ): String {

    if ( s.length < limit ) return s

    return s.substring( 0, limit.toInt() ) + "..."

}
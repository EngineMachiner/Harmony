package com.enginemachiner.harmony

import java.awt.Color
import kotlin.random.Random

fun randomColor(): Color {

    val h = Random.nextInt( 100 + 1 ) * 0.01f
    val b = Random.nextInt( 75, 100 + 1 ) * 0.01f

    return Color.getHSBColor( h, 1f, b )

}
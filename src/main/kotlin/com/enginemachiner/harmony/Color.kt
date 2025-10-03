package com.enginemachiner.harmony

import java.awt.Color
import java.awt.Color.getHSBColor
import kotlin.random.Random

fun randomColor( saturation: Float = 1f, brightness: Float = 1f ): Color {

    val hue = Random.nextFloat();               return getHSBColor( hue, saturation, brightness )

}
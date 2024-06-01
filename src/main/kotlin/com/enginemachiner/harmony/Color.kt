package com.enginemachiner.harmony

import net.minecraft.util.math.Vec3f

fun randomColor(): Vec3f {

    val color = MutableList(3) { 0f }

    for ( i in 0..2 ) color[i] = ( 0..255 ).random().toFloat()

    return Vec3f( color[0], color[1], color[2] )

}
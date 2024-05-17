package com.enginemachiner.harmony

import kotlin.math.PI

/** Convert degree to radians. */
fun rad(angle: Double): Double { return angle % 360 * 2 * PI / 360 }

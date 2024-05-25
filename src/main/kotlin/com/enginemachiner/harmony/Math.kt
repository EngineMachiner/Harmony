package com.enginemachiner.harmony

import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vec3i
import kotlin.math.PI

/** Convert degree to radians. */
fun rad(angle: Double): Double { return angle % 360 * 2 * PI / 360 }

/** Convert a Vec3f into a Vec3d. */
fun vec3d(vec3f: Vec3f): Vec3d {

    return Vec3d( vec3f.x.toDouble(), vec3f.y.toDouble(), vec3f.z.toDouble() )

}

/** Convert a Vec3f into a Vec3i. */
fun vec3i(vec3f: Vec3f): Vec3i {

    return Vec3i( vec3f.x.toInt(), vec3f.y.toInt(), vec3f.z.toInt() )

}
package com.enginemachiner.harmony

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.math.Vec3f
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max

@Environment(EnvType.CLIENT)
fun randomColor(): Vec3f {

    val color = MutableList(3) { 0f }

    for ( i in 0..2 ) color[i] = ( 0..255 ).random().toFloat()

    return Vec3f( color[0], color[1], color[2] )

}

/** Can do color transitions or tweenings. */
@Environment(EnvType.CLIENT)
class ColorTween(

    private val timeOn: Float = 0.5f,
    private val brightnessTime: Float = 0.5f,

    ) {

    private var time = 0f

    private val length = timeOn + brightnessTime


    private var hueDelta = 0f;      private var hue = 0f

    private var brightness = 1f


    var startColor: Color = Color.WHITE
    var endColor: Color = Color.BLACK

    private var hsb1 = FloatArray(3)
    private var hsb2 = FloatArray(3)

    private var color = startColor


    init { init() }

    fun init() {

        Color.RGBtoHSB( startColor.red, startColor.green, startColor.blue, hsb1 )

        Color.RGBtoHSB( endColor.red, endColor.green, endColor.blue, hsb2 )

        hue = hsb1[0];      color = startColor;     hueDelta = hueDelta()

    }

    fun isDone(): Boolean { return time > length }

    fun reset() { time = 0f;    color = startColor;     brightness = hsb1[2] }

    fun color(): Color {

        time += delta();        brightness(); hue();  setColor()

        return color

    }

    private fun delta(): Float { return client().lastFrameDuration / 20 }

    private fun setColor() {

        var saturation = 1f;        if ( startColor == Color.WHITE ) saturation = 0f

        color = Color( Color.HSBtoRGB( hue, saturation, brightness ) )

    }

    private fun brightness() {

        if ( time < timeOn ) return

        val delta = delta() * 0.825f / brightnessTime

        brightness = max( brightness - delta, 0f )

    }

    private fun hue() {

        val delta = delta() * hueDelta / length

        if ( time > timeOn ) hue = hsb2[0];         hue += delta

    }

    private fun hueDelta(): Float {

        val direction1 = hsb2[0] - hsb1[0]

        var direction2 = 1 - abs(direction1)

        direction2 *= - direction1 / abs(direction1)


        var delta = direction1

        if ( abs(direction2) < abs(direction1) ) delta = direction2


        if ( startColor == endColor ) delta = 1f


        return delta * 1.75f

    }

}
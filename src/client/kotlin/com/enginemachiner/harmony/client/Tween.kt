package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.TICKS_PER_SECOND
import net.minecraft.util.math.MathHelper.lerp
import net.minecraft.util.math.MathHelper.lerpAngleDegrees
import java.awt.Color
import java.awt.Color.RGBtoHSB

interface Tweenable {

    fun restart();          fun isDone(): Boolean

    fun delay( delay: Float );          fun update( delta: Float )

}

// Duration in seconds.

open class Tween(

    private val start: Float,           private val end: Float,
    private val duration: Float,        private val easing: Easing = Easing.LINEAR

) : Tweenable {

    private var delay = 0f;         private var time = 0f

    override fun delay( delay: Float ) { this.delay = delay }

    fun progress(): Float {

        var t = time - delay;           t = t.coerceIn( 0f, 1f );           return t / duration

    }

    open fun interpolate( t: Float ) = lerp( t, start, end )

    fun value(): Float {

        var t = progress();         t = easing.calculate(t);         return interpolate(t)

    }

    override fun update( delta: Float ) { if ( !isDone() ) time += delta / TICKS_PER_SECOND }

    override fun restart() { time = 0f };           override fun isDone() = time >= duration

}

class AngleTween(

    private val start: Float,           private val end: Float,
    duration: Float,                    easing: Easing = Easing.LINEAR

) : Tween( start, end, duration, easing ) {

    override fun interpolate( t: Float ) = lerpAngleDegrees( t, start, end )
    
}

class ColorTween(

    start: Color,               end: Color,
    duration: Float,            easing: Easing = Easing.LINEAR

) : Tweenable {

    // The hue is in range [0, 1] for RGBtoHSB() and lerpAngleDegrees() will work fine with it.
    private val startHSB = RGBtoHSB( start.red, start.green, start.blue, null )
    private val endHSB = RGBtoHSB( end.red, end.green, end.blue, null )

    private val hue = AngleTween( startHSB[0], endHSB[0], duration, easing )
    private val saturation = Tween( startHSB[1], endHSB[1], duration, easing )
    private val brightness = Tween( startHSB[2], endHSB[2], duration, easing )

    private val group = TweenGroup( hue, saturation, brightness )

    fun value(): Color {

        val hue = hue.value()

        val saturation = saturation.value();           val brightness = brightness.value()

        return Color.getHSBColor( hue, saturation, brightness )

    }

    override fun delay( delay: Float ) { group.delay(delay) }

    override fun update( delta: Float ) { group.update(delta) }

    override fun restart() { group.restart() };         override fun isDone() = group.isDone()

}

class TweenGroup( vararg tweens: Tweenable ) : Tweenable {

    private val tweens = tweens.toMutableList()

    fun add( vararg tween: Tweenable ): TweenGroup { tweens.addAll(tween); return this }

    override fun delay( delay: Float ) { tweens.forEach { it.delay(delay) } }

    override fun update( delta: Float ) { tweens.forEach { it.update(delta) } }

    override fun restart() { tweens.forEach { it.restart() } }

    override fun isDone() = tweens.all { it.isDone() }

}

class TweenSequence( vararg tweens: Tweenable ) : Tweenable {

    fun currentTween() = tweens[i]

    private var i = 0;          private val tweens = tweens.toMutableList()

    fun then( vararg tween: Tweenable ): TweenSequence { tweens.addAll(tween); return this }

    override fun delay( delay: Float ) { currentTween().delay(delay) }

    // Move to next tween when current tween is done.
    override fun update( delta: Float ) {

        if ( isDone() ) return;         val tween = currentTween()

        tween.update(delta);          if ( tween.isDone() ) i++

    }

    override fun restart() { i = 0;          tweens.forEach { it.restart() } }

    override fun isDone() = i >= tweens.size

}
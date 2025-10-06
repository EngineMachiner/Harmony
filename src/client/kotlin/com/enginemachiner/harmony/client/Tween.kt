package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.TICKS_PER_SECOND
import net.minecraft.util.math.MathHelper.lerp
import net.minecraft.util.math.MathHelper.lerpAngleDegrees
import java.awt.Color
import java.awt.Color.RGBtoHSB

/**
 * Base interface for objects that can be animated over time.
 */
interface Tweenable {

    /** Restarts the animation from the beginning. */
    fun restart()

    /** Returns true if the animation has completed. */
    fun isDone(): Boolean

    /** Sets a delay in seconds before the animation starts. */
    fun delay( delay: Float )

    /** Updates the animation by the given delta time in ticks. */
    fun update( delta: Float )

}

/**
 * Interpolates a value from [start] to [end] over a specified [duration].
 *
 * @param start The starting value.
 * @param end The ending value.
 * @param duration The duration of the tween in seconds.
 * @param easing The easing enum to apply to the interpolation.
 */
open class Tween(

    private val start: Float,           private val end: Float,
    private val duration: Float,        private val easing: Easing = Easing.LINEAR

) : Tweenable {

    private var delay = 0f;         private var time = 0f

    override fun delay( delay: Float ) { this.delay = delay }

    /**
     * Returns the current progress of the tween as a value between 0 and 1.
     */
    fun progress(): Float {

        var t = time - delay;           t = t.coerceIn( 0f, 1f );           return t / duration

    }

    /**
     * Interpolates between start and end values using the given progress [t].
     * Can be overridden for custom interpolation behavior.
     */
    open fun interpolate( t: Float ) = lerp( t, start, end )

    /** Returns the current interpolated value with easing applied. */
    fun value(): Float {

        var t = progress();         t = easing.calculate(t);         return interpolate(t)

    }

    override fun update( delta: Float ) { if ( !isDone() ) time += delta / TICKS_PER_SECOND }

    override fun restart() { time = 0f };           override fun isDone() = time >= duration

}

/**
 * A specialized tween for interpolating angles in degrees.
 * Uses angular interpolation to handle wrapping around 360 degrees correctly.
 */
class AngleTween(

    private val start: Float,           private val end: Float,
    duration: Float,                    easing: Easing = Easing.LINEAR

) : Tween( start, end, duration, easing ) {

    override fun interpolate( t: Float ) = lerpAngleDegrees( t, start, end )
    
}

/**
 * A tween for smoothly interpolating between two colors in HSB color space.
 * Interpolates hue as an angle for smooth color transitions.
 */
class ColorTween(

    start: Color,               end: Color,
    duration: Float,            easing: Easing = Easing.LINEAR

) : Tweenable {

    private val startHSB = RGBtoHSB( start.red, start.green, start.blue, null )
    private val endHSB = RGBtoHSB( end.red, end.green, end.blue, null )

    private val hue = AngleTween( startHSB[0], endHSB[0], duration, easing )
    private val saturation = Tween( startHSB[1], endHSB[1], duration, easing )
    private val brightness = Tween( startHSB[2], endHSB[2], duration, easing )

    private val group = TweenGroup( hue, saturation, brightness )

    /** Returns the current interpolated color. */
    fun value(): Color {

        val hue = hue.value()

        val saturation = saturation.value();           val brightness = brightness.value()

        return Color.getHSBColor( hue, saturation, brightness )

    }

    override fun delay( delay: Float ) { group.delay(delay) }

    override fun update( delta: Float ) { group.update(delta) }

    override fun restart() { group.restart() };         override fun isDone() = group.isDone()

}

/**
 * Groups multiple tweens to run simultaneously.
 * The group is considered done when all tweens are done.
 *
 * @param tweens The tweens to group together.
 */
class TweenGroup( vararg tweens: Tweenable ) : Tweenable {

    private val tweens = tweens.toMutableList()

    /**
     * Adds additional tweens to this group.
     * @return This group for method chaining.
     */
    fun add( vararg tween: Tweenable ): TweenGroup { tweens.addAll(tween); return this }

    override fun delay( delay: Float ) { tweens.forEach { it.delay(delay) } }

    override fun update( delta: Float ) { tweens.forEach { it.update(delta) } }

    override fun restart() { tweens.forEach { it.restart() } }

    override fun isDone() = tweens.all { it.isDone() }

}

/**
 * Runs multiple tweens in sequence, one after another.
 * Each tween starts when the previous one completes.
 *
 * @param tweens The tweens to run in sequence.
 */
class TweenSequence( vararg tweens: Tweenable ) : Tweenable {

    /**
     * Returns the currently active tween in the sequence.
     */
    fun currentTween() = tweens[i]

    private var i = 0;          private val tweens = tweens.toMutableList()

    /**
     * Adds additional tweens to run after the current sequence.
     * @return This sequence for method chaining.
     */
    fun then( vararg tween: Tweenable ): TweenSequence { tweens.addAll(tween); return this }

    override fun delay( delay: Float ) { currentTween().delay(delay) }

    override fun update( delta: Float ) {

        if ( isDone() ) return;         val tween = currentTween()

        tween.update(delta);          if ( tween.isDone() ) i++

    }

    override fun restart() { i = 0;          tweens.forEach { it.restart() } }

    override fun isDone() = i >= tweens.size

}
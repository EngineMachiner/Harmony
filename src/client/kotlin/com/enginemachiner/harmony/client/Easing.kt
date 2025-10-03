package com.enginemachiner.harmony.client

import net.minecraft.util.math.MathHelper.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Easing {

    LINEAR { override fun calculate( t: Float ) = t },

    /** Accelerates from zero velocity. */
    EASE_IN_SINE {

        override fun calculate( t: Float ) = - cos( t * PI / 2f ) + 1f

    },

    /** Decelerates to zero velocity. */
    EASE_OUT_SINE {

        override fun calculate( t: Float ) = sin( t * PI / 2f )

    },

    /** Accelerates until halfway, then decelerates. */
    EASE_IN_OUT_SINE {

        override fun calculate( t: Float ): Float {

            val t = cos( t * PI ) - 1f;           return - t / 2

        }

    };

    abstract fun calculate( t: Float ): Float

}
package com.enginemachiner.harmony.client

import com.mojang.blaze3d.systems.RenderSystem.setShaderTexture
import net.minecraft.client.gui.DrawableHelper.drawTexture
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import java.awt.Color

interface Positionable {

    var pos: Vec2f;           fun size(): Vec2f

    fun pos(): Vec2f { val offset = size().multiply(-0.5f);          return pos.add(offset) }

    fun center( size: Vec2f ) { centerX( size.x ); centerY( size.y ) }

    fun centerX( width: Float ) { pos = Vec2f( width * 0.5f, pos.y ) }

    fun centerY( height: Float ) { pos = Vec2f( pos.x, height * 0.5f ) }

}

/** Wrapper for clickable widgets with automatic text trimming. */
open class Widget( initPos: Vec2f, initSize: Vec2f, message: Text, val widget: ClickableWidget ) : Positionable {

    override var pos = initPos;         var size = initSize;            override fun size() = size

    private fun updatePos() { widget.x = pos().x.toInt();          widget.y = pos().y.toInt() }

    private fun onWidthChange() {

        val width = size.x.toInt();         val changed = widget.width != width;            if ( !changed ) return

        widget.width = width;         widget.message = formerMessage

    }

    private var formerMessage = message

    open fun setMessage( message: Text ) {

        formerMessage = message;                val string = message.string

        val renderer = textRenderer();          val trimmed = renderer.trimToWidth( string, widget.width )

        val text = Text.of( trimmed );          widget.message = text

    }

    open fun render( matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float ) {

        updatePos();          onWidthChange();         widget.render( matrices, mouseX, mouseY, delta )

    }

    private companion object { const val WIDGET_HEIGHT = 20f }

}

/** Rendering text with automatic line wrapping. */
abstract class HarmonyText( initPos: Vec2f, initText: Text, maxWidth: Float? = null ) : Positionable {

    var text = initText;    set(value) { field = value;    refresh() }
    var maxWidth = maxWidth;    set(value) { field = value;    refresh() }

    private val renderer = textRenderer()

    override var pos = initPos;         var lineHeight = renderer.fontHeight * 1f

    override fun size(): Vec2f {

        val string = text.string;           val height = lineHeight * lines.size

        val width = maxWidth ?: renderer.getWidth(string)

        return Vec2f( width.toFloat(), height )

    }

    private var lines = lines();            private fun refresh() { lines = lines() }

    fun lines(): List<OrderedText> {

        val width = size().x.toInt();           return renderer.wrapLines( text, width )

    }

    private fun lineY( i: Int ): Float {

        val halfHeight = size().y * 0.5f;           val y = pos().y

        return y - halfHeight + i * lineHeight

    }

    abstract fun render( matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float )

}

open class Texture( initPos: Vec2f, initSize: Vec2f, var id: Identifier ) : Positionable {

    override var pos = initPos;         var size = initSize;            override fun size() = size

    var texturePos: Vec2f = Vec2f.ZERO;            var textureSize = size

    fun drawTexture( matrices: MatrixStack ) {

        val x1 = pos().x.toInt();            val y1 = pos().y.toInt()
        val w1 = size().x.toInt();           val h1 = size().y.toInt()

        val x2 = texturePos.x;                  val y2 = texturePos.y
        val w2 = textureSize.x.toInt();         val h2 = textureSize.y.toInt()

        drawTexture( matrices, x1, y1, x2, y2, w1, h1, w2, h2 )

    }

    open fun render( matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float ) {

        setShaderTexture( 0, id );          drawTexture(matrices);          restoreColor()

    }

    fun isHovered( mouseX: Int, mouseY: Int ): Boolean {

        val x1 = pos.x;                     val y1 = pos.y
        val x2 = pos.x + size.x;            val y2 = pos.y + size.y

        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2

    }

    private companion object {

        fun restoreColor() { setShaderColor( Color.WHITE ) }

    }

}
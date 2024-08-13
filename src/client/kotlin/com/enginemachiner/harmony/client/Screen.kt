package com.enginemachiner.harmony.client

import com.enginemachiner.harmony.BasedOn
import com.enginemachiner.harmony.ModID
import com.enginemachiner.harmony.SLOT_SIZE
import com.enginemachiner.harmony.ScreenRefresher.netID
import com.enginemachiner.harmony.client.HarmonyWidget.Companion.offset
import com.enginemachiner.harmony.shorten
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.*
import net.minecraft.client.util.Clipboard
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color
import kotlin.math.min

fun currentScreen(): Screen? { return client().currentScreen }

fun isOnScreen(): Boolean { return currentScreen() != null }

interface ScreenRefresher {

    fun refresh()

    /** Updates the handled screens. */
    fun updateScreens() { Sender(netID).toServer() }

    companion object  {

        fun networking() {

            Receiver(netID).registerEmpty {

                val screen = currentScreen()

                if ( screen !is ScreenRefresher ) return@registerEmpty

                screen.refresh()

            }

        }

    }

}


typealias SliderAction = (Slider) -> Unit

abstract class HarmonyScreen<T: ScreenHandler>(

    handler: T, playerInventory: PlayerInventory, text: Text

) : HandledScreen<T>( handler, playerInventory, text ), ModID {

    private val sliders = mutableListOf<Slider>()

    fun addSlider(slider: Slider) { sliders.add(slider);    addDrawableChild(slider) }

    @BasedOn("ParentElement.mouseDragged()")
    fun isFocusedDragged( mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double ): Boolean {

        val focused = focused ?: return false


        val dragged = isDragging && button == 0

        if ( !dragged ) return false


        return focused.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)

    }

    @BasedOn("ParentElement.mouseRelease()")
    fun wasElementDragged( mouseX: Double, mouseY: Double, button: Int ): Boolean {

        isDragging = false;     val hovered = hoveredElement(mouseX, mouseY)

        val present = hovered.filter { it.mouseReleased(mouseX, mouseY, button) }

        return present.isPresent

    }

}


private typealias ButtonFunction = (Button) -> Unit

open class Button(

    x: Float, y: Float,     w: Float, h: Float,         private val messageFunction: () -> String,

    private val init: ButtonFunction = {},          action: ButtonFunction

) : ButtonWidget(

    offset(x, w), offset(y, h),         w.toInt(), min( h.toInt(), MAX_HEIGHT ),

    Text.of( messageFunction() ),       { it as Button; action(it) },

    DEFAULT_NARRATION_SUPPLIER

), HarmonyWidget {

    constructor(

        x: Float, y: Float,     w: Float, h: Float,     message: String,

        init: ButtonFunction = {},          action: ButtonFunction

    ) : this( x, y, w, h, { message }, init, action )

    init { init() };        private fun init() { init(this); updateMessage() }

    fun updateMessage( new: String = messageFunction() ) {

        val next = trim(new);        message = Text.of(next)

    }

    /** Gets a trimmed message to fit in the widget. */
    private fun trim(s: String): String {   return shorten( s, width / 7f )    }

    private companion object { const val MAX_HEIGHT = 20 }

}

class ClearButton(

    x: Float, y: Float,     w: Float, h: Float,     textField: TextField,

    message: String,        init: ButtonFunction = {}

) : Button( x, y, w, h, message, init, { textField.text = "" } )

class CopyButton(

    x: Float, y: Float,     w: Float, h: Float,     textField: TextField,

    message: String,        init: ButtonFunction = {}

) : Button( x, y, w, h, message, init, { action(textField) } ) {

    private companion object {

        val clipboard = Clipboard();            val handle = client().window.handle

        fun action( field: TextField ) { clipboard.setClipboard( handle, field.text ) }

    }

}


open class TextField(

    x: Float, y: Float, w: Float, h: Float,

    message: String,    renderer: TextRenderer,    private val init: (TextField) -> Unit

) : TextFieldWidget( renderer, offset(x, w), offset(y, h), w.toInt(), min( h.toInt(), MAX_HEIGHT ), Text.of(message) ), HarmonyWidget {

    init { init() };        private fun init() { init(this) }

    private companion object { const val MAX_HEIGHT = 20 }

}


abstract class Slider(

    x: Float, y: Float,     w: Float, h: Float,     value: Float = 0f,

    private val action: SliderAction = {}

) : SliderWidget( offset(x, w), offset(y, h), w.toInt(), min( h.toInt(), MAX_HEIGHT ), Text.of(""), value.toDouble() ), HarmonyWidget {

    abstract fun format(value: Double): String

    init { updateMessage() };       fun value(): Float { return value.toFloat() }

    final override fun updateMessage() {

        val formatted = format(value);       message = Text.of(formatted)

    }

    override fun applyValue() { action(this) }

    private companion object { const val MAX_HEIGHT = 20 }

}


open class Checkbox(

    x: Float, y: Float,     w: Float, h: Float,

    message: String,        checked: Boolean,         private val init: (Checkbox) -> Unit = {}

) : CheckboxWidget( offset(x, w), offset(y, h), w.toInt(), min( h.toInt(), MAX_HEIGHT ), Text.of(message), checked ), HarmonyWidget {

    init { init() };        private fun init() { init(this) };          fun check() { super.onPress() }

    private companion object { const val MAX_HEIGHT = 20 }

}


private typealias TextFunction = (RenderText) -> Unit

open class RenderText( private val init: TextFunction = {} ) : HarmonyText {

    var x = 0f;      var y = 0f;        var onRender: TextFunction = {}

    lateinit var text: String;          private lateinit var renderer: TextRenderer

    fun init(renderer: TextRenderer) { init("", renderer) }

    fun init( text: String,     renderer: TextRenderer,     onRender: TextFunction = {} ) {

        this.text = text;       this.renderer = renderer

        setPos(0f);             this.onRender = onRender;           init(this)

    }

    fun width(): Float { return renderer.getWidth(text).toFloat() }

    fun height(): Float { return renderer.fontHeight.toFloat() }

    fun setPos( widget: ClickableWidget? ) {

        this.x = widget!!.x.toFloat();        this.y = widget.y.toFloat()

    }

    open fun render( matrices: MatrixStack, color: Int = Color.WHITE.rgb ) {

        onRender(this);     renderer.draw( matrices, text, x, y, color )

    }

}

class FadingText( init: TextFunction ) : RenderText(init) {

    private val colorTween = ColorTween()

    fun render( matrices: MatrixStack ) {

        if ( colorTween.isDone() ) return

        val color = colorTween.color()

        super.render( matrices, color.rgb )

    }

    fun reset() { colorTween.reset() }

}


/** Remember to always define your texture size first! */
open class Texture( val id: Identifier, private val init: (Texture) -> Unit = {} ) : HarmonyTexture {

    var x = 0f;       var y = 0f
    var u = 0f;       var v = 0f
    var w = 0f;       var h = 0f

    var textureWidth = 0f;       var textureHeight = 0f

    open fun init() { init(this) }

    fun setTextureSize() { textureWidth = w;       textureHeight = h }

    fun color(color: FloatArray) {

        RenderSystem.setShaderColor( color[0], color[1], color[2], color[3] )

    }

    fun color(color: Color) {

        val r = color.red / 255f;       val g = color.green / 255f
        val b = color.blue / 255f;     val a = color.alpha / 255f

        RenderSystem.setShaderColor( r, g, b, a )

    }

    open fun draw(matrices: MatrixStack) {

        RenderSystem.setShaderTexture( 0, id )


        val x = x.toInt();      val y = y.toInt()

        val w1 = w.toInt();        val h1 = h.toInt()

        val w2 = textureWidth.toInt();      val h2 = textureHeight.toInt()


        DrawableHelper.drawTexture( matrices, x, y, u, v, w1, h1, w2, h2 )

        color( Color.WHITE )

    }

    fun isClickOutsideBounds( mouseX: Double, mouseY: Double ): Boolean {

        val a = w * 0.5f;           val b = h * 0.5f

        val x = x + a;              val y = y + b

        return mouseX < x - a || mouseX > x + a
                || mouseY > y + b || mouseY < y - b

    }

}


abstract class Offset<T> {

    abstract fun offset( x: Float, w: Float ): T

}

interface HarmonyWidget {

    fun addPos(a: Float) { this as ClickableWidget

        x += a.toInt();   y += a.toInt()

    }

    fun addPos( x: Float, y: Float ) { this as ClickableWidget

        this.x += x.toInt();   this.y += y.toInt()

    }

    fun setPos(a: Float) { this as ClickableWidget

        x = a.toInt();    y = a.toInt()

    }

    fun setPos( x: Float, y: Float ) { this as ClickableWidget

        this.x = x.toInt();    this.y = y.toInt()

    }


    fun center( screenWidth: Int, screenHeight: Int ) {

        centerX(screenWidth);   centerY(screenHeight)

    }

    fun centerX(screenWidth: Int) { this as ClickableWidget

        x = offset( screenWidth * 0.5f, width.toFloat() )

    }

    fun centerY(screenHeight: Int) { this as ClickableWidget

        y = offset( screenHeight * 0.5f, height.toFloat() )

    }


    fun offsetX(w: Float) { this as ClickableWidget

        x = offset( x.toFloat(), - w * 2 )

    }

    fun offsetY(h: Float) { this as ClickableWidget

        y = offset( y.toFloat(), - h * 2 )

    }

    companion object : Offset<Int>() {

        override fun offset( x: Float, w: Float ): Int {

            val x = x - w * 0.5f;       return x.toInt()

        }

    }

}

interface HarmonyText {

    fun addPos(a: Float) { this as RenderText;         x += a;   y += a }

    fun addPos( x: Float, y: Float ) { this as RenderText;         this.x += x;  this.y += y }

    fun setPos(a: Float) { this as RenderText;         x = a;   y = a }

    fun setPos( x: Float, y: Float ) { this as RenderText;         this.x = x;   this.y = y }

    fun setPos(texture: Texture) { setPos( texture.x, texture.y ) }

    fun center(screenWidth: Int, screenHeight: Int ) {

        centerX(screenWidth);   centerY(screenHeight)

    }

    fun centerX(screenWidth: Int) { this as RenderText

        x = offset( screenWidth * 0.5f, width() )

    }

    fun centerY(screenHeight: Int) { this as RenderText

        y = offset( screenHeight * 0.5f, height() )

    }


    fun offsetX(w: Float) { this as RenderText

        x = offset( x, - w * 2 )

    }

    fun offsetY(h: Float) { this as RenderText

        y = offset( y, - h * 2 )

    }

    companion object : Offset<Float>() {

        override fun offset( x: Float, w: Float ): Float { return x - w * 0.5f }

    }

}


interface HarmonyTexture {

    fun cut( w: Float, h: Float ) { this as Texture

        this.w = w;     this.h = h

    }

    fun setSize(a: Float) { this as Texture

        w = a;      h = a;       setTextureSize()

    }

    fun setSize( w: Float, h: Float ) { this as Texture

        cut(w, h);         setTextureSize()

    }


    fun addPos(a: Float) { this as Texture;        x += a;   y += a }

    fun addPos( x: Float, y: Float ) { this as Texture;        this.x += x;  this.y += y }

    fun setPos(a: Float) { this as Texture;        x = a;   y = a }

    fun setPos( x: Float, y: Float ) { this as Texture;        this.x = x;   this.y = y }

    /** Set the texture based off a centered screen and a slot. */
    fun setPos( screenX: Int, screenY: Int, slot: Slot ) { this as Texture

        val x = screenX + slot.x;       val y = screenY + slot.y

        this.x = x.toFloat();         this.y = y.toFloat()


        val size = - SLOT_SIZE * 0.5f + 1f

        offsetX(size);     offsetY(size)

    }


    fun center(screenWidth: Int, screenHeight: Int ) {

        centerX(screenWidth);   centerY(screenHeight)

    }

    fun centerX(screenWidth: Int) { this as Texture

        x = offset( screenWidth * 0.5f, w )

    }

    fun centerY(screenHeight: Int) { this as Texture

        y = offset( screenHeight * 0.5f, h )

    }


    fun offsetX(w: Float) { this as Texture

        x = offset( x, - w * 2 )

    }

    fun offsetY(h: Float) { this as Texture

        y = offset( y, - h * 2 )

    }

    fun offset( x: Float, w: Float ): Float { return Companion.offset(x, w) }

    companion object : Offset<Float>() {

        override fun offset( x: Float, w: Float ): Float { return x - w * 0.5f }

    }

}




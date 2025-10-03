package com.enginemachiner.harmony

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.CriterionConditions
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

/** Advancement builder for custom advancements in Data Generation. */
abstract class AdvancementBuilder {

    lateinit var advancement: Advancement

    open val parent: AdvancementBuilder? = null

    abstract val icon: Item
    abstract val title: Text;           abstract val description: Text

    open val frame = AdvancementFrame.TASK
    open val background = DEFAULT_BACKGROUND

    open val showToast = false;             open val announceToChat = false
    open val hidden = false

    abstract val criterion: String
    abstract val conditions: CriterionConditions

    open fun configureBuilder( builder: Advancement.Builder ) {}

    abstract fun id(): String

    fun build( consumer: Consumer<Advancement> ): Advancement {

        val builder = Advancement.Builder.create()
            .display( icon, title, description, background, frame, showToast, announceToChat, hidden )
            .criterion( criterion, conditions )

        parent?.let { builder.parent( it.advancement ) };           configureBuilder(builder)

        advancement = builder.build( consumer, id() );          return advancement

    }

    private companion object {

        val DEFAULT_BACKGROUND = Identifier("textures/gui/advancements/backgrounds/adventure.png")

    }

}

class ModAdvancement( private val mod: Mod ) {

    abstract inner class Builder( val path: String ) : AdvancementBuilder() {

        val translation = mod.translation.advancement

        override val title = translation.title(path)
        override val description = translation.description(path)
        override val criterion = path + "_trigger"

        override fun id() = mod.id(path).toString()

    }

}

package com.enginemachiner.harmony

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.CriterionConditions
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

/**
 * Abstract builder to create custom advancements when generating data.
 * Provides a structured way to define advancement properties and to build them.
 */
abstract class AdvancementBuilder {

    /** Set after calling [build]. */
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

    /**
     * Hook for additional builder configuration.
     * Override to add custom criteria or other builder settings.
     */
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

/** Advancement builder factory for a specific mod. */
class ModAdvancement( private val mod: Mod ) {

    /**
     * Base builder for mod-specific advancements.
     * Automatically configures title, description, and ID based on the mod and path.
     *
     * @param path The advancement path within the mod namespace.
     */
    abstract inner class Builder( val path: String ) : AdvancementBuilder() {

        val translation = mod.translation.advancement

        override val title = translation.title(path)
        override val description = translation.description(path)
        override val criterion = path + "_trigger"

        override fun id() = mod.id(path).toString()

    }

}

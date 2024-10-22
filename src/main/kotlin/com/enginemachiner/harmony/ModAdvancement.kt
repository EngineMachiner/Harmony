package com.enginemachiner.harmony

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

/** Advancement builder for custom mod advancements in Data Generation. */
abstract class ModAdvancement : ModID {

    abstract val icon: Item;        open val parent: ModAdvancement? = null

    open var name = "";            open var key = "";           init { init() }

    private fun init() { name = className();        key = name }

    open fun title(): Text { return Translation.Advancement.title(key) }
    open fun description(): Text { return Translation.Advancement.description(key) }

    open val frame = AdvancementFrame.TASK
    open val background = Identifier("textures/gui/advancements/backgrounds/adventure.png")

    open val toast = false;          open val announce = false
    open val hidden = false

    private var advancement: Advancement? = null

    abstract fun conditions(): AbstractCriterionConditions

    fun build( consumer: Consumer<Advancement>, criterion: String = name ): Advancement {

        val builder = Advancement.Builder.create()
            .display( icon, title(), description(), background, frame, toast, announce, hidden )
            .criterion( criterion, conditions() )

        if ( parent != null ) builder.parent( parent!!.advancement )

        val advancement = builder.build( consumer, "$MOD_NAME/$name" )

        this.advancement = advancement;         return advancement

    }

}

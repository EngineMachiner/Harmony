package com.enginemachiner.harmony

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

private typealias Conditions = AbstractCriterionConditions

/** Advancement builder for custom mod advancements in Data Generation. */
abstract class ModAdvancement : ModID {

    open val name: String = lazy { className() }.value

    abstract val icon: ItemStack

    open val parent: ModAdvancement? = null


    open val key: String = lazy { name }.value

    open fun title(): Text { return Translation.Advancement.title(key) }
    open fun description(): Text { return Translation.Advancement.description(key) }


    open val frame = AdvancementFrame.TASK
    open val background = Identifier("textures/gui/advancements/backgrounds/adventure.png")


    private val toast = false;          private val announce = false
    private val hidden = false


    private var advancement: Advancement? = null


    fun build( consumer: Consumer<Advancement>, conditions: Conditions, criterion: String = name ): Advancement {

        val advancement = Advancement.Builder.create()
            .display( icon, title(), description(), background, frame, toast, announce, hidden )
            .parent( parent?.advancement ).criterion( criterion, conditions )
            .build( consumer, "$MOD_NAME/$name" )

        this.advancement = advancement;         return advancement

    }

}
package com.enginemachiner.harmony

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import java.util.function.Consumer

private typealias Conditions = AbstractCriterionConditions

/** Advancement builder for custom mod advancements in Data Generation. */
open class ModAdvancement(

    val name: String,               titleKey: String,
    descriptionKey: String,         val icon: ItemStack,
    val parent: Advancement?

) {

    private val title = Translation.Advancement.title(titleKey)
    private val description = Translation.Advancement.title(descriptionKey)

    var frame = AdvancementFrame.TASK

    var background = Identifier("textures/gui/advancements/backgrounds/adventure.png")

    private var toast = false;          private var announce = false
    private var hidden = false

    fun toast(): ModAdvancement { toast = true;     return this }
    fun announce(): ModAdvancement { announce = true;     return this }
    fun hidden(): ModAdvancement { hidden = true;     return this }

    fun build( consumer: Consumer<Advancement>, conditions: Conditions, criterion: String = name ) {

        val builder = Advancement.Builder.create()

        if ( parent != null ) builder.parent(parent)

        builder.display( icon, title, description, background, frame, toast, announce, hidden )
            .criterion( criterion, conditions ).build( consumer, "$MOD_NAME/$name" )

    }

}
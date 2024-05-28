package com.enginemachiner.harmony

import net.minecraft.util.Identifier
import kotlin.reflect.KClass

/** The mod's name for titles. */
val MOD_TITLE = ModID.name()

/** The mod's name lowercase. */
val MOD_NAME = MOD_TITLE.lowercase()

/** The mod's name to show up on chat. */
val CHAT_MOD_TITLE = "§3 [${ MOD_NAME.uppercase() }]: §f"

fun modID(id: String): Identifier { return Identifier( MOD_NAME, id ) }

fun textureID(id: String): Identifier { return Identifier( MOD_NAME, "textures/$id" ) }

/** IDs, identifiers and mod names helper. */
interface ModID {

    fun classID(): Identifier { return modID( className() ) }

    fun className(): String { return Companion.className( this::class ) }

    fun netID(id: String): Identifier {

        val className = className();    return Identifier("$className:$id")

    }

    companion object {

        private lateinit var MOD_NAME: String;          private var hasName = false

        private const val NAME_ERROR = "[ERROR]: You can only ModID.setName() once!"

        internal fun name(): String { return MOD_NAME }

        /** Initializes and sets the mod name once. */
        fun init(name: String) {

            if ( hasName ) { modPrint( NAME_ERROR ); return }

            hasName = true;     MOD_NAME = name

        }

        fun className( kClass: KClass<*> ): String { return Parser(kClass).build() }

        private class Parser( private val kClass: KClass<*> ) {

            var result = kClass.simpleName!!

            fun build(): String {

                companion();    enchantment();    block();      screen()

                entity();       symbols();      return result

            }

            fun block() { replace("Block"); replace("BlockEntity") }
            fun enchantment() { replace("Enchantment") }
            fun screen() { replace("ScreenHandler") }
            fun entity() { replace("Entity") }

            fun symbols() {

                result = result.replaceFirstChar { result[0].lowercase() }


                val regex = Regex("([A-Z])([a-z])")

                result = result.replace( regex, "_$1$2" ).lowercase()

            }

            fun companion() {

                val isCompanion = kClass.isCompanion;           if ( !isCompanion ) return


                val qualified = kClass.qualifiedName!!;         val regex = Regex("[A-Z].+")


                result = regex.find(qualified)!!.value
                    .replace( ".Companion", "" )

            }

            fun replace( endsWith: String ) {

                if ( !result.endsWith(endsWith) ) return

                result = result.replace( endsWith, "" )

            }

        }

    }

}
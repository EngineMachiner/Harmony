package com.enginemachiner.harmony

import com.google.gson.GsonBuilder
import org.apache.commons.lang3.SystemUtils
import java.io.BufferedReader
import java.io.File
import kotlin.reflect.KClass

/** Get the system separator. */
private fun separator(): Char { return if ( SystemUtils.IS_OS_WINDOWS ) ';' else ':' }

private val regex = Regex("\\$[A-Z]*")

/** Get the environment / system path if there is one. */
fun envPath(path: String): String {

    if ( path.isEmpty() || path[0] != '$' ) return path


    var envKey = regex.find(path)!!.value

    val sub = path.substringAfter("$envKey/")


    envKey = envKey.substringAfter("$")

    val directories = System.getenv(envKey) ?: return path


    val list = directories.split( separator() )


    list.forEach {

        val path = it + "\\$sub";       if ( File(path).exists() ) return path

    }


    return path

}

/** Read output from a buffered reader. */
fun output( reader: BufferedReader ): String? {

    var output = reader.readLine()

    while (true) {

        val line = reader.readLine() ?: break;          output += line

    }

    return output

}

/** Files that are in the mod's folder. */
open class ModFile( private val name: String ) : File(name) {

    init { init() }

    private fun init() {

        val name = name.replace( "/", "\\" )

        if ( name.startsWith("$MOD_NAME\\") || name.isEmpty() ) return


        modPrint(ERROR)

        setExecutable(false);     setReadable(false);     setWritable(false)

    }

    private companion object { val ERROR = Message.parse("error.denied") }

}

abstract class ConfigFile<T : Any>(

    s: String,      private val defaults: KClass<T>

): File("$DIRECTORY$s.json") {

    protected var map = mutableMapOf<String, Any>()

    protected var data: T? = null


    abstract fun setDefaults()


    private fun init() { create(); read() }

    init { init() }     open fun check() {}


    fun data(): T { return data!! }


    private fun read() {

        data = GSON.fromJson( reader(), defaults.java );    check()

        map = GSON.fromJson( reader(), map::class.java )

    }

    fun write() { writeText( GSON.toJson(data) ) }


    open fun canCreate(): Boolean { return true }

    private fun create() {

        if ( !canCreate() || exists() || length() > 0L ) return

        createNewFile();        setDefaults()

    }


    fun keys(): Set<String> { return map.keys }

    fun keys( kClass: KClass<*> ): List<String> {

        return map.keys.filter { kClass.isInstance( map[it] ) }

    }


    fun toMap() { check();      map = json( data, map::class ) }

    fun set( key: String, value: Any ) {

        map[key] = value;       data = json(map, defaults);     toMap()

    }


    companion object {

        private val GSON = GsonBuilder().setPrettyPrinting().create()

        private val DIRECTORY = "config/$MOD_NAME/"

        fun <T: Any> json( src: Any?, kClass: KClass<T> ): T {

            val toJson = GSON.toJson(src)

            return GSON.fromJson( toJson, kClass.java )

        }

        fun checkDirectory() {

            val dir = File(DIRECTORY);      if ( !dir.exists() ) dir.mkdirs()

        }

    }

}
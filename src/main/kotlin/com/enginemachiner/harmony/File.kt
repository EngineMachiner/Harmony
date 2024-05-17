package com.enginemachiner.harmony

import com.google.gson.GsonBuilder
import org.apache.commons.lang3.SystemUtils
import java.io.BufferedReader
import java.io.File
import kotlin.reflect.KClass

/** Get the environment / system path if there is one. */
fun envPath(path: String): String {

    if ( path.isEmpty() || path[0] != '$' ) return path


    val envKey = Regex("\\$[A-Z]*").find(path)!!.value

    val subPath = path.replace( "$envKey/", "" )

    val directories = System.getenv( envKey.substring(1) ) ?: return path


    var separator = ':';    if ( SystemUtils.IS_OS_WINDOWS ) separator = ';'

    val list = directories.split(separator)


    list.forEach {

        val path = it + "\\$subPath";       if ( File(path).exists() ) return path

    }


    return path

}

/** Files that are in the honkytones folder. Like midi files. */
open class ModFile( private val name: String ) : File(name) {

    init { init() }

    private fun init() {

        val name = name.replace( "/", "\\" )

        if ( name.startsWith("$MOD_NAME\\") || name.isEmpty() ) return


        warnUser("error.denied")

        setExecutable(false);     setReadable(false);     setWritable(false)

    }

}

/** Read output from a buffered reader. */
fun output( reader: BufferedReader ): String? {

    var output = reader.readLine()

    while (true) {

        val line = reader.readLine() ?: break;      output += line

    }

    return output

}

abstract class ConfigFile<T : Any>(

    s: String,      private val defaults: KClass<T>

): File("$DIRECTORY$s.json") {

    protected var map = mutableMapOf<String, Any>()

    protected var data: T? = null


    private fun init() { create(); read() }

    init { init() }     open fun check() {}


    fun data(): T { return data!! }


    private fun read() {

        data = GSON.fromJson( reader(), defaults.java );    check()

        map = GSON.fromJson( reader(), map::class.java )

    }

    fun write() { writeText( GSON.toJson(data) ) }


    open fun canCreateFile(): Boolean { return true }

    private fun create() {

        if ( !canCreateFile() || exists() || length() > 0L ) return

        createNewFile();        setDefaults()

    }


    abstract fun setDefaults()


    fun keys(): Set<String> { return map.keys }

    fun keys( kClass: KClass<*>): List<String> {

        return map.keys.filter { kClass.isInstance( map[it] ) }

    }

    fun set( key: String, value: Any ) {

        map[key] = value;       data = json(map, defaults);     toMap()

    }

    fun toMap() { check();      map = json( data, map::class ) }


    companion object {

        private val GSON = GsonBuilder().setPrettyPrinting().create()

        private val DIRECTORY = "config/$MOD_NAME/"

        fun <T: Any> json( src: Any?, kClass: KClass<T>): T {

            val toJson = GSON.toJson(src)

            return GSON.fromJson( toJson, kClass.java )

        }

        fun checkConfigDirectory() {

            val dir = File(DIRECTORY);      if ( !dir.exists() ) dir.mkdirs()

        }

    }

}
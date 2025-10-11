package com.enginemachiner.harmony

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

private val LOADER: FabricLoader = FabricLoader.getInstance()

/** Checks if a path exists and returns it, or null if it doesn't. */
private fun existingPath( parent: String, child: String ): String? {

    val isEmpty = child.isEmpty();          val file = if (isEmpty) File(parent) else File( parent, child )

    val exists = file.exists();             return if (exists) file.path else null

}

/**
 * Resolves a path that may start with an environment variable.
 * @param path Path in format "$ENV_VAR/optional/subpath"
 */
fun envPath( path: String ): String {

    if ( path.isEmpty() || path[0] != '$' ) return path


    val s = File.separator

    val (envVar, subPath) = path.substring(1).split( s, limit = 2 )

    val envValue = System.getenv(envVar) ?: return path


    val contains = envValue.contains(s);            var envPath: String? = existingPath( envValue, subPath )

    if (contains) envPath = envValue.split(s).find { existingPath( it, subPath ) != null }

    return envPath ?: path

}

/**
 * Provides secure file access within the mod's directory.
 * Prevents path traversal attacks by validating paths stay within the mod directory.
 */
class SecureFileAccessor( mod: Mod, userPath: String ) {

    private val id = mod.id

    /** The root path for this mod's files. */
    val path: Path = LOADER.gameDir.resolve(id)

    private val resolvedPath = path.resolve( userPath ).normalize()

    /** The canonical (real) path after resolving symlinks and normalizing. */
    val canonicalPath: Path = resolvedPath.toRealPath()

    /** Returns true if the path is valid and within the mod directory. */
    fun isValid(): Boolean = canonicalPath.startsWith(path)

    fun toFile(): File = canonicalPath.toFile()

}

/**
 * Manages configuration files for the mod.
 * Handles loading, saving, and creating config files with default values.
 */
class ConfigManager( mod: Mod ) {

    private val id = mod.id

    private val directory = LOADER.configDir.resolve(id)

    init { Files.createDirectories(directory) }

    private fun path( fileName: String ) = directory.resolve("$fileName.json")

    /** Creates a new config file with default values. */
    private fun <T: Any> create( fileName: String, defaults: KClass<T> ): T {

        val data = defaults.createInstance();           save( fileName, data )

        return data

    }

    /**
     * Loads a config file or creates it with default values if it doesn't exist
     * @param fileName Config file name without extension
     * @param defaults Class that provides default config values
     */
    fun <T: Any> get( fileName: String, defaults: KClass<T> ): T {

        val path = path(fileName)


        val exists = Files.exists(path)

        if ( !exists ) return create( fileName, defaults )


        val json = Files.readString(path);          val java = defaults.java

        return GSON.fromJson( json, java )

    }

    /**
     * Saves a config to file.
     * @param fileName Config file name without extension
     * @param data The config object to save
     */
    fun <T> save( fileName: String, data: T ) {

        val path = path(fileName);          val json = GSON.toJson(data)

        Files.writeString( path, json )

    }

    private companion object {

        val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

    }

}

/** Provides file and configuration management utilities for the mod. */
class File( private val mod: Mod ) {

    /**
     * Creates a secure file accessor for the given path.
     *
     * @param userPath The user-provided path to access.
     * @return A SecureFileAccessor instance.
     */
    fun secureAccessor( userPath: String ) = SecureFileAccessor( mod, userPath )

    /** The configuration manager for this mod. */
    val configManager = ConfigManager(mod)

}
package ch.carveship2elastik.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ConfigTest {
    fun loadFromFile(path: Path): Config {
        val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
        mapper.registerModule(KotlinModule()) // Enable Kotlin support

        return Files.newBufferedReader(path).use {
            mapper.readValue(it, Config::class.java)
        }
    }
    @Test
    fun testConfigLoad() {
        val config = loadFromFile(Paths.get("src/test/resources/config.yml"))
        assertEquals("user", config.username)
    }
}
package ch.carveship2elastik.config

import org.junit.Assert.*
import org.junit.Test
import java.io.FileReader
import com.esotericsoftware.yamlbeans.YamlReader



class ConfigTest {
    @Test
    fun testConfigLoad() {
        val yaml = YamlReader(FileReader("src/test/resources/config.yml"))
        val config = yaml.read(Config::class.java)
        assertEquals("user", config.username)
        assertEquals("undertow", config.logfiles[0].application)
    }
}
package ch.carveship2elastik

import ch.carveship2elastik.config.Config
import java.awt.SystemColor.info
import java.util.stream.Collectors
import sun.font.LayoutPathImpl.getPath
import ch.carveship2elastik.ship2elastik.SmartFileReader
import java.io.FileReader
import com.esotericsoftware.yamlbeans.YamlReader
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val config = loadFromFile(Paths.get("src/test/resources/config.yml"))
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val reader = SmartFileReader(config.logfiles.get(0).getPath(), 0, config.getInterval(), config.getBulkSize())
    val parser = LineParser(config.getLogfiles().get(0).getApplication(), config.getLogfiles().get(0).getTimeFormat())

    reader.setListener { lines, pos -> sender.send(lines.stream().map(???({ parser.parse(it) })).collect(Collectors.toList<T>())) }

    Log2Elastic.logger.info("Start...")
    reader.run()
    Log2Elastic.logger.info("Exit ...")

}

fun loadFromFile(path: Path): Config {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    return Files.newBufferedReader(path).use {
        mapper.readValue(it, Config::class.java)
    }
}

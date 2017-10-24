package ch.carveship2elastik

import ch.carveship2elastik.config.Config
import ch.carveship2elastik.ship2elastik.NewLinesListener
import ch.carveship2elastik.ship2elastik.SmartFileReader
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val config = loadFromFile(Paths.get("src/test/resources/config.yml"))
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val parser = LineParser(config.logfiles.get(0).application, config.logfiles.get(0).timeFormat)
    val reader = SmartFileReader(config.logfiles.get(0).path, 0, config.interval, config.bulkSize, { lines, _ -> sender.send(lines.map(parser::parse)) })

    reader.run()

}

fun loadFromFile(path: Path): Config {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    return Files.newBufferedReader(path).use {
        mapper.readValue(it, Config::class.java)
    }
}

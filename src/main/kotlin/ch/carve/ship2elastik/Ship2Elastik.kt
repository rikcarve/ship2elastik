package ch.carve.ship2elastik

import ch.carve.ship2elastik.config.Config
import com.esotericsoftware.yamlbeans.YamlReader
import java.io.FileReader

fun main(args: Array<String>) {
    val yaml = YamlReader(FileReader("src/main/resources/config.yml"))
    val config = yaml.read(Config::class.java)
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val parser = LineParser(config.logfiles[0].application, config.logfiles[0].timeFormat)
    val reader = SmartFileReader(config.logfiles[0].path, 0, config.interval, config.bulkSize, { lines, _ -> sender.send(lines.map(parser::parse)) })

    // save application: timestamp + position
    reader.run()

}

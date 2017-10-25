package ch.carveship2elastik

import ch.carveship2elastik.config.Config
import ch.carveship2elastik.ship2elastik.SmartFileReader
import com.esotericsoftware.yamlbeans.YamlReader
import java.io.FileReader

fun main(args: Array<String>) {
    val yaml = YamlReader(FileReader("src/test/resources/config.yml"))
    val config = yaml.read(Config::class.java)
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val parser = LineParser(config.logfiles.get(0).application, config.logfiles.get(0).timeFormat)
    val reader = SmartFileReader(config.logfiles.get(0).path, 0, config.interval, config.bulkSize, { lines, _ -> sender.send(lines.map(parser::parse)) })

    reader.run()

}

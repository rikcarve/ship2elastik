package ch.carve.ship2elastik

import ch.carve.ship2elastik.config.Config
import com.esotericsoftware.yamlbeans.YamlReader
import java.io.FileReader

fun main(args: Array<String>) {
    var filename = "src/main/resources/config.yml"
    if (args.isNotEmpty()) {
        filename = args[0]
    }
    val yaml = YamlReader(FileReader(filename))
    val config = yaml.read(Config::class.java)
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val parser = LineParser(config.logfiles[0].application, config.logfiles[0].timeFormat)
    val reader = SmartFileReader(config.logfiles[0].path, 0, config.interval, config.bulkSize, { lines, _ -> sender.send(lines.map(parser::parse)) })

    // save application: timestamp + position
    reader.run()

}

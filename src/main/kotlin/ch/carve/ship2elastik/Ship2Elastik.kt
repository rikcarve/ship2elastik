package ch.carve.ship2elastik

import ch.carve.ship2elastik.config.Config
import com.esotericsoftware.yamlbeans.YamlReader
import java.io.FileReader
import java.io.RandomAccessFile

fun main(args: Array<String>) {
    var filename = "src/main/resources/config.yml"
    if (args.isNotEmpty()) {
        filename = args[0]
    }
    val yaml = YamlReader(FileReader(filename))
    val config = yaml.read(Config::class.java)
    val sender = HttpElasticSender(config.url, config.username, config.password, config.index)
    val parser = LineParser(config.logfiles[0].application, config.logfiles[0].timeFormat)
    val file = RandomAccessFile(config.logfiles[0].application + ".pos", "rw")
    val startPosition = if (file.length() > 0) file.readLong() else 0
    val reader = SmartFileReader(
            config.logfiles[0].path,
            startPosition,
            config.interval,
            config.bulkSize,
            { lines, position -> file.seek(0); file.writeLong(position); sender.send(lines.mapNotNull(parser::parse)) }
    )

    // save application: timestamp + position
    reader.run()
}

package ch.carve.ship2elastik

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.ArrayList
import org.slf4j.LoggerFactory

class SmartFileReader(filePath: String, private var position: Long, private val interval: Long, private val bulkSize: Int, private val listener: NewLinesListener) {
    private val file: File = File(filePath)
    private var stop = false

    fun stop() {
        stop = true
    }

    fun run() {
        while (!stop) {
            try {
                val newLength = file.length()
                if (newLength > position) {
                    readFile()
                } else if (newLength < position) {
                    // e.g. after rollover -> restart at 0
                    position = 0
                    readFile()
                }
                sleep()
            } catch (e: IOException) {
                logger.error("Excpetion occurred: ", e)
                stop()
            }
        }
    }

    @Throws(IOException::class)
    private fun readFile() {
        RandomAccessFile(file, "r").use({ raf ->
            raf.seek(position)
            val lines = ArrayList<String>()
            var count = 0
            var line = raf.readLine()
            while (line != null) {
                lines.add(line)
                if (++count >= bulkSize) {
                    notify(raf, lines)
                    lines.clear()
                    count = 0
                }
                line = raf.readLine()
            }
            notify(raf, lines)
        })
    }

    @Throws(IOException::class)
    private fun notify(raf: RandomAccessFile, lines: List<String>) {
        val newPosition = raf.filePointer
        if (listener(lines, newPosition)) {
            position = newPosition
        } else {
            throw IOException("Notification failed")
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(interval)
        } catch (e: InterruptedException) {
            logger.info("Interrupted, stop", e)
            stop()
            Thread.currentThread().interrupt()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SmartFileReader::class.java)
    }
}

package ch.carveship2elastik

import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class LineParser(application:String, dateTimeFormat:String) {
    private val dtf:DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)
    private val dtfLength:Int = dateTimeFormat.length
    private val application:String = application

    fun parse(line:String):LogMessage {
        val now = OffsetDateTime.now()
        val datetime = LocalTime.parse(line.substring(0, dtfLength), dtf).atOffset(now.offset).atDate(now.toLocalDate())
        return LogMessage(datetime, application, line, emptyMap())
    }
}
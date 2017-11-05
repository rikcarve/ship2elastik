package ch.carve.ship2elastik

import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class LineParser(private val application: String, dateTimeFormat:String) {
    private val dtf:DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)
    private val dtfLength:Int = dateTimeFormat.length

    fun parse(line:String): LogMessage? {
        val now = OffsetDateTime.now()
        try {
            val datetime = LocalTime.parse(line.substring(0, dtfLength), dtf).atOffset(now.offset).atDate(now.toLocalDate())
            return LogMessage(datetime, application, line, emptyMap())
        } catch (e : DateTimeParseException) {
        }
        return null
    }
}
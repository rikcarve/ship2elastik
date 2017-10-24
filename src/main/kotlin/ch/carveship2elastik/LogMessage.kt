package ch.carveship2elastik

import java.time.OffsetDateTime

data class LogMessage(
        val datetime: OffsetDateTime,
        val application: String,
        val logMessage: String,
        val fields: Map<String, String>
)

package ch.carve.ship2elastik

import java.io.IOException
import javax.json.Json
import org.slf4j.LoggerFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Optional

class HttpElasticSender(private val url: String, username: String, password: String, private val index: String) {
    private val client: OkHttpClient = OkHttpClient()
    private val authToken: String = Base64.getEncoder().encodeToString((username + ":" + password).toByteArray())
    private val indexDateFormat = DateTimeFormatter.ofPattern("YYYY.MM.dd")
    private val environment = Optional.ofNullable(System.getenv("FO_ENV")).orElse(System.getProperty("FO_ENV", "dev"))

    fun send(messages: List<LogMessage>): Boolean {
        val builder = StringBuilder()
        for (message in messages) {
            val header = Json.createObjectBuilder()
                    .add("index", Json.createObjectBuilder()
                            .add("_index", calcIndex(index))
                            .add("_type", "doc")
                            .build())
                    .build().toString()
            val content = createJsonFromMessage(message)
            builder.append(header)
            builder.append(System.lineSeparator())
            builder.append(content)
            builder.append(System.lineSeparator())
        }
        val request = Request.Builder()
                .url(url + "/_bulk")
                .header("Authorization", "Basic " + authToken)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(JSON, builder.toString()))
                .build()
        logger.debug(builder.toString())
        return sendHttpRequest(request)
    }

    private fun calcIndex(indexPrefix: String) : String {
        return indexPrefix + "-" + LocalDate.now().format(indexDateFormat)
    }

    private fun sendHttpRequest(request: Request): Boolean {
        try {
            client.newCall(request).execute().use {
                logger.info("Response status code: {}", it.code())
                return it.isSuccessful
            }
        } catch (e : IOException) {
            logger.warn("Failed to send HTTP request", e)
            return false
        }
    }

    private fun createJsonFromMessage(message: LogMessage): String {
        return Json.createObjectBuilder()
                .add("datetime", message.datetime.toString())
//                .add("@timestamp", message.datetime.toString())
                .add("type", "logs")
                .add("environment", environment)
                .add("application", message.application)
                .add("logmessage", message.logMessage)
                .build().toString()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HttpElasticSender::class.java)
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}

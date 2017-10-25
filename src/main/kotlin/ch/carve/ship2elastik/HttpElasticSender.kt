package ch.carve.ship2elastik

import java.io.IOException
import java.util.Base64
import javax.json.Json
import org.slf4j.LoggerFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class HttpElasticSender(private val url: String, username: String, password: String, private val index: String) {
    private val client: OkHttpClient = OkHttpClient()
    private val authToken: String = Base64.getEncoder().encodeToString((username + ":" + password).toByteArray())

    fun send(messages: List<LogMessage>): Boolean {
        val builder = StringBuilder()
        for (message in messages) {
            val header = Json.createObjectBuilder()
                    .add("index", Json.createObjectBuilder()
                            .add("_index", index)
                            .add("_type", "log")
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
                .post(RequestBody.create(JSON, builder.toString()))
                .build()
        return sendHttpRequest(request)
    }

    private fun sendHttpRequest(request: Request): Boolean {
        try {
            return client.newCall(request).execute().isSuccessful()
        } catch (e: IOException) {
            logger.error("Bulk request exception", e)
        }
        return false
    }

    private fun createJsonFromMessage(message: LogMessage): String {
        return Json.createObjectBuilder()
                .add("@datetime", message.datetime.toString())
                .add("application", message.application)
                .add("logmessage", message.logMessage)
                .build().toString()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HttpElasticSender::class.java)
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}

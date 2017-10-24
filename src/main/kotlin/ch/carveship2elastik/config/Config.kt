package ch.carveship2elastik.config

data class Config (
    val url: String? = null,
    val username: String? = null,
    val password: String? = null,
    val bulkSize: Int = 0,
    val interval: Int = 0,
    val index: String? = null,
    val logfiles: List<Logfile>? = null
)

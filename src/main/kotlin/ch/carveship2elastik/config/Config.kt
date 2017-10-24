package ch.carveship2elastik.config

data class Config (
    val url: String,
    val username: String,
    val password: String,
    val bulkSize: Int = 0,
    val interval: Long = 0,
    val index: String,
    val logfiles: List<Logfile>
)

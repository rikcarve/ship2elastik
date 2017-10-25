package ch.carve.ship2elastik.config

data class Config (
    var url: String = "",
    var username: String = "",
    var password: String = "",
    var bulkSize: Int = 0,
    var interval: Long = 0,
    var index: String = "",
    var logfiles: List<Logfile> = emptyList()
)

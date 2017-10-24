package ch.carveship2elastik.ship2elastik

public interface NewLinesListener {
    fun onNewLines(lines:List<String>, lastPosition:Long):Boolean
}

package site.jonus.savog.core.util

object History {
    fun getContent(
        originMap: Map<String, Any?>?,
        updateMap: Map<String, Any?>,
        title: String? = null
    ): String {
        val sb = StringBuilder()

        val logs = originMap?.mapNotNull {
            val originValue = it.value?.toString() ?: ""
            val updateValue = updateMap[it.key]?.toString() ?: ""
            if (updateMap[it.key] != null && originValue != updateValue) "${it.key}: $originValue → $updateValue" else null
        }

        if (logs != null && logs.isNotEmpty()) {
            title?.let { sb.append("[변경] ($title)\n") } ?: sb.append("[변경]\n")
            sb.append(logs.joinToString("\n"))
        }

        return sb.toString()
    }
}

package interpreter

import java.util.*

data class EvaluationResult(val result: Any?) {
    override fun toString(): String {
        return when (result) {
            null -> "nil"
            is Double -> "%.2f".format(Locale.US, result).trimEnd('0').trimEnd('.').trimEnd(',')
            else -> result.toString()
        }
    }
}
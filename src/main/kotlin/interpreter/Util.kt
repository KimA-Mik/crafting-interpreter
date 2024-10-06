package interpreter

import java.util.*

fun Any?.stringify(): String {
    return when (this) {
        null -> "nil"
        is Double -> "%.2f".format(Locale.US, this).trimEnd('0').trimEnd('.')
        else -> this.toString()
    }
}
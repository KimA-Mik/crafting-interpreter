import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println("Usage: ./your_program.sh tokenize <filename>")
        exitProcess(1)
    }

    val command = args[0]
    val filename = args[1]

    if (command != "tokenize") {
        System.err.println("Unknown command: ${command}")
        exitProcess(1)
    }

    val fileContents = File(filename).readText()

    for (c in fileContents) {
        when (c) {
            '(' -> println("LEFT_PAREN ( null")
            ')' -> println("RIGHT_PAREN ) null")
            '{' -> println("LEFT_BRACE { null")
            '}' -> println("RIGHT_BRACE } null")
            '*' -> println("STAR * null")
            '.' -> println("DOT . null")
            ',' -> println("COMMA , null")
            '+' -> println("PLUS + null")
            else -> continue
        }
    }

    println("EOF  null")
}

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println("Usage: ./your_program.sh tokenize <filename>")
        exitProcess(1)
    }

    val command = args[0]
    val filename = args[1]

    when (command) {
        "tokenize" -> tokenize(filename)
        "parse" -> parse(filename)
        else -> unknownCommand(command)
    }
}

private fun tokenize(filename: String) {
    val fileContents = File(filename).readText()

    val lexer = Lexer(fileContents)
    val tokens = lexer.tokenise()

    tokens.forEach {
        println(it)
    }

    if (lexer.lexicalError) {
        exitProcess(65)
    }
}

private fun parse(filename: String) {
    val fileContents = File(filename).readText()

    val lexer = Lexer(fileContents)
    val tokens = lexer.tokenise()

    tokens.forEach {
        when (it.type) {
            TokenType.FALSE -> println(it.lexeme)
            TokenType.NIL -> println(it.lexeme)
            TokenType.TRUE -> println(it.lexeme)
            TokenType.NUMBER -> println(it.literal)
            else -> {}
        }
    }

    if (lexer.lexicalError) {
        exitProcess(65)
    }
}

private fun unknownCommand(command: String) {
    System.err.println("Unknown command: $command")
    exitProcess(1)
}
import interpreter.Interpreter
import lexer.Lexer
import parser.Parser
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
        "evaluate" -> evaluate(filename)
        else -> unknownCommand(command)
    }
}

fun evaluate(filename: String) {
    val fileContents = File(filename).readText()

    val lexer = Lexer(fileContents)
    val tokens = lexer.tokenise()
    if (lexer.lexicalError) {
        exitProcess(65)
    }
    val parser = Parser(tokens)

    val expression = parser.parse()

    if (parser.parserError || expression == null) {
        exitProcess(65)
    }

    val interpreter = Interpreter()
    when (val res = interpreter.evaluate(expression)) {
        null -> println("nil")
        is Double -> println("%.2f".format(res).trimEnd('0').trimEnd('.').trimEnd(','))
        else -> println(res)
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
    val parser = Parser(tokens)

    val expression = parser.parse()
    expression?.let {
        println(it)
    }

    if (lexer.lexicalError || parser.parserError) {
        exitProcess(65)
    }
}

private fun unknownCommand(command: String) {
    System.err.println("Unknown command: $command")
    exitProcess(1)
}
import interpreter.Interpreter
import lexer.Lexer
import parser.Parser
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println("Usage: ./your_program.sh [tokenize] [parse] [evaluate] [run]  <filename>")
        exitProcess(1)
    }

    val command = args[0]
    val filename = args[1]

    when (command) {
        "tokenize" -> tokenize(filename)
        "parse" -> parse(filename)
        "evaluate" -> evaluate(filename)
        "run" -> run(filename)
        else -> unknownCommand(command)
    }
}

fun run(filename: String) {
    val fileContents = File(filename).readText()

    val lexer = Lexer(fileContents)
    val tokens = lexer.tokenise()
    if (lexer.lexicalError) {
        exitProcess(65)
    }

    val parser = Parser(tokens)
    val statements = parser.parseStatements()
    if (parser.parserError) {
        exitProcess(65)
    }

    val interpreter = Interpreter()
    interpreter.execute(statements)
    if (interpreter.evaluationError) {
        exitProcess(70)
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
    val expression = parser.parseExpression()
    if (parser.parserError || expression == null) {
        exitProcess(65)
    }

    val interpreter = Interpreter()
    val result = interpreter.interpretExpression(expression)
    if (interpreter.evaluationError) {
        exitProcess(70)
    }

    result?.let {
        println(it)
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
    if (lexer.lexicalError) {
        exitProcess(65)
    }

    val parser = Parser(tokens)
    val expression = parser.parseExpression()
    expression?.let {
        println(it)
    }

    if (parser.parserError) {
        exitProcess(65)
    }
}

private fun unknownCommand(command: String) {
    System.err.println("Unknown command: $command")
    exitProcess(1)
}
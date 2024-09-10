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

    var lexicalErrors = false
    val tokens = mutableListOf<Token>()

    val fileContents = File(filename).readText()

    var line = 1
    for (c in fileContents) {
        if (c == '\n') {
            line += 1
            continue
        }

        val type = getTokenType(c)
        if (type == null) {
            lexicalErrors = true
            reportUnexpectedCharacter(c, line)
        } else {
            tokens.add(Token(type = type, string = c.toString()))
        }
    }
    tokens.add(Token(type = TokenType.EOF, string = String()))

    tokens.forEach {
        println(it)
    }

    if (lexicalErrors) {
        exitProcess(65)
    }
}

fun getTokenType(c: Char): TokenType? {
    return when (c) {
        '(' -> TokenType.LEFT_PAREN
        ')' -> TokenType.RIGHT_PAREN
        '{' -> TokenType.LEFT_BRACE
        '}' -> TokenType.RIGHT_BRACE
        '*' -> TokenType.STAR
        '.' -> TokenType.DOT
        ',' -> TokenType.COMMA
        '+' -> TokenType.PLUS
        '-' -> TokenType.MINUS
        ';' -> TokenType.SEMICOLON
        else -> null
    }
}

fun reportUnexpectedCharacter(character: Char, line: Int) {
    System.err.println("[line $line] Error: Unexpected character: $character")
}
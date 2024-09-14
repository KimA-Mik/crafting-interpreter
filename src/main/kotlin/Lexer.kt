class Lexer(val text: String) {
    private var position = 0
    var lexicalError = false
        private set

    fun resetState() {
        lexicalError = false
        position = 0
    }

    fun tokenise(): List<Token> {
        resetState()
        val tokens = mutableListOf<Token>()

        var pastTokenPosition = 0
        var line = 1
        while (hasNext()) {
            val c = currentChar()
            if (c == '\n') {
                line += 1
                continue
            }

            val type = getNextTokenType()
            if (type == null) {
                lexicalError = true
                reportUnexpectedCharacter(c, line)
            } else {
                tokens.add(Token(type = type, string = text.substring(pastTokenPosition, position)))
            }
            pastTokenPosition = position
        }
        tokens.add(Token(type = TokenType.EOF, string = String()))
        return tokens
    }

    private fun hasNext() = position < text.length
    private fun currentChar() = text[position]

    private fun getNextTokenType(): TokenType? {
        val c = text[position]
        val t = when (c) {
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
            '=' -> parseEquals()
            '!' -> parseBang()
            '<' -> parseLess()
            '>' -> parseGreater()
            else -> null
        }
        position += 1
        return t
    }

    private fun parseEquals(): TokenType {
        val next = position + 1

        return if (expectChat(next, EQUALS)) {
            position += 1
            TokenType.EQUAL_EQUAL
        } else {
            TokenType.EQUAL
        }
    }

    private fun parseBang(): TokenType {
        val next = position + 1

        return if (expectChat(next, EQUALS)) {
            position += 1
            TokenType.BANG_EQUAL
        } else {
            TokenType.BANG
        }
    }

    private fun parseLess(): TokenType {
        val next = position + 1
        return if (expectChat(next, EQUALS)) {
            position += 1
            TokenType.LESS_EQUAL
        } else {
            TokenType.LESS
        }
    }

    private fun parseGreater(): TokenType {
        val next = position + 1
        return if (expectChat(next, EQUALS)) {
            position += 1
            TokenType.GREATER_EQUAL
        } else {
            TokenType.GREATER
        }
    }

    private fun expectChat(pos: Int, c: Char): Boolean {
        return pos < text.length && text[pos] == c
    }

    private fun reportUnexpectedCharacter(character: Char, line: Int) {
        System.err.println("[line $line] Error: Unexpected character: $character")
    }

    companion object {
        private const val EQUALS = '='
    }
}
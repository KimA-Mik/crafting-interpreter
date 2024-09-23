class Lexer(val text: String) {
    private var position = 0
    private var line = 1

    var lexicalError = false
        private set

    fun resetState() {
        lexicalError = false
        position = 0
        line = 1
    }

    fun tokenise(): List<Token> {
        resetState()
        val tokens = mutableListOf<Token>()

        var token: Token? = null
        while (token?.type != TokenType.EOF) {
            token = getNextToken()
            token?.let {
                tokens.add(token)
            }
        }
        return tokens
    }

    private fun hasNext() = position < text.length
    private fun currentChar() = text[position]

    private fun getNextToken(advance: Boolean = true): Token? {
        skipWhile { it.isWhitespace() }
        if (!hasNext()) return Tokens.DEFAULT_EOF
        val token = when (val c = currentChar()) {
            LEFT_PAREN -> Tokens.DEFAULT_LEFT_PAREN
            RIGHT_PAREN -> Tokens.DEFAULT_RIGHT_PAREN
            LEFT_BRACE -> Tokens.DEFAULT_LEFT_BRACE
            RIGHT_BRACE -> Tokens.DEFAULT_RIGHT_BRACE
            DOT -> Tokens.DEFAULT_DOT
            COMMA -> Tokens.DEFAULT_COMMA
            STAR -> Tokens.DEFAULT_STAR
            SLASH -> parseSlash()
            PLUS -> Tokens.DEFAULT_PLUS
            MINUS -> Tokens.DEFAULT_MINUS
            SEMICOLON -> Tokens.DEFAULT_SEMICOLON
            EQUAL -> parseEquals()
            BANG -> parseBang()
            LESS -> parseLess()
            GREATER -> parseGreater()
            else -> {
                lexicalError = true
                position += 1
                reportUnexpectedCharacter(c, line)
                getNextToken(false)
            }
        }

        if (advance) {
            position += 1
        }

        return token
    }

    private fun parseSlash(): Token? {
        val next = position + 1
        if (expectChar(next, SLASH)) {
            skipWhile { it != '\n' }
            return getNextToken(false)
        } else {
            return Tokens.DEFAULT_SLASH
        }
    }

    private fun parseEquals(): Token {
        val next = position + 1

        return if (expectChar(next, EQUAL)) {
            position += 1
            Tokens.DEFAULT_EQUAL_EQUAL
        } else {
            Tokens.DEFAULT_EQUAL
        }
    }

    private fun parseBang(): Token {
        val next = position + 1

        return if (expectChar(next, EQUAL)) {
            position += 1
            Tokens.DEFAULT_BANG_EQUAL
        } else {
            Tokens.DEFAULT_BANG
        }
    }

    private fun parseLess(): Token {
        val next = position + 1
        return if (expectChar(next, EQUAL)) {
            position += 1
            Tokens.DEFAULT_LESS_EQUAL
        } else {
            Tokens.DEFAULT_LESS
        }
    }

    private fun parseGreater(): Token {
        val next = position + 1
        return if (expectChar(next, EQUAL)) {
            position += 1
            Tokens.DEFAULT_GREATER_EQUAL
        } else {
            Tokens.DEFAULT_GREATER
        }
    }

    private fun skipWhile(condition: (Char) -> Boolean) {
        while (hasNext() && condition(currentChar())) {
            if (currentChar() == '\n') line += 1
            position += 1
        }
    }

    private fun expectChar(pos: Int, c: Char): Boolean {
        return pos < text.length && text[pos] == c
    }

    private fun reportUnexpectedCharacter(character: Char, line: Int) {
        System.err.println("[line $line] Error: Unexpected character: $character")
    }

    companion object {
        private const val LEFT_PAREN = '('
        private const val RIGHT_PAREN = ')'
        private const val LEFT_BRACE = '{'
        private const val RIGHT_BRACE = '}'
        private const val DOT = '.'
        private const val COMMA = ','
        private const val STAR = '*'
        private const val EQUAL = '='
        private const val SLASH = '/'
        private const val PLUS = '+'
        private const val MINUS = '-'
        private const val SEMICOLON = ';'
        private const val BANG = '!'
        private const val LESS = '<'
        private const val GREATER = '>'
    }

    private object Tokens {
        val DEFAULT_LEFT_PAREN = Token(type = TokenType.LEFT_PAREN, string = LEFT_PAREN.toString())
        val DEFAULT_RIGHT_PAREN = Token(type = TokenType.RIGHT_PAREN, string = RIGHT_PAREN.toString())
        val DEFAULT_LEFT_BRACE = Token(type = TokenType.LEFT_BRACE, string = LEFT_BRACE.toString())
        val DEFAULT_RIGHT_BRACE = Token(type = TokenType.RIGHT_BRACE, string = RIGHT_BRACE.toString())
        val DEFAULT_DOT = Token(type = TokenType.DOT, string = DOT.toString())
        val DEFAULT_COMMA = Token(type = TokenType.COMMA, string = COMMA.toString())
        val DEFAULT_STAR = Token(type = TokenType.STAR, string = STAR.toString())
        val DEFAULT_EQUAL = Token(type = TokenType.EQUAL, string = EQUAL.toString())
        val DEFAULT_EQUAL_EQUAL = Token(type = TokenType.EQUAL_EQUAL, string = "$EQUAL$EQUAL")
        val DEFAULT_SLASH = Token(type = TokenType.SLASH, string = SLASH.toString())
        val DEFAULT_PLUS = Token(type = TokenType.PLUS, string = PLUS.toString())
        val DEFAULT_MINUS = Token(type = TokenType.MINUS, string = MINUS.toString())
        val DEFAULT_SEMICOLON = Token(type = TokenType.SEMICOLON, string = SEMICOLON.toString())
        val DEFAULT_BANG = Token(type = TokenType.BANG, string = BANG.toString())
        val DEFAULT_BANG_EQUAL = Token(type = TokenType.BANG_EQUAL, string = "$BANG$EQUAL")
        val DEFAULT_LESS = Token(type = TokenType.LESS, string = LESS.toString())
        val DEFAULT_LESS_EQUAL = Token(type = TokenType.LESS_EQUAL, string = "$LESS$EQUAL")
        val DEFAULT_GREATER = Token(type = TokenType.GREATER, string = GREATER.toString())
        val DEFAULT_GREATER_EQUAL = Token(type = TokenType.GREATER_EQUAL, string = "$GREATER$EQUAL")

        val DEFAULT_EOF = Token(type = TokenType.EOF, string = String())
    }
}
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
            tokens.add(token)
        }
        return tokens
    }

    private fun hasNext() = position < text.length
    private fun currentChar() = text[position]

    private fun getNextToken(advance: Boolean = true): Token {
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
            DOUBLE_QUOTES -> parseStringLiteral()
            else -> {
                if (c.isDigit()) {
                    return parseNumber()
                }
                if (c.isIdentifierStart()) {
                    return parseIdentifier()
                }

                position += 1
                lexicalError = true
                reportUnexpectedCharacter(c, line)
                getNextToken(false)
            }
        }

        if (advance) {
            position += 1
        }

        return token
    }

    private fun parseIdentifier(): Token {
        val start = position
        skipWhile { it.isIdentifier() }

        return Token(
            type = TokenType.IDENTIFIER,
            lexeme = text.substring(start, position),
        )
    }

    private fun parseNumber(): Token {
        val start = position
        skipWhile { it.isDigit() }
        if (hasNext() && currentChar() == DOT) {
            position += 1
            skipWhile { it.isDigit() }
        }

        val lexeme = text.substring(start, position)

        return Token(
            type = TokenType.NUMBER,
            lexeme = lexeme,
            literal = lexeme.toDoubleOrNull()?.toString()
        )
    }

    private fun parseStringLiteral(): Token {
        val start = position
        position += 1
        skipWhile { it != DOUBLE_QUOTES }

        if (!hasNext()) {
            lexicalError = true
            reportUnterminatedString()
            return getNextToken(false)
        }

        return Token(
            type = TokenType.STRING,
            lexeme = text.substring(start, position + 1),
            literal = text.substring(start + 1, position),
        )
    }

    private fun parseSlash(): Token {
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

    private fun reportUnterminatedString() {
        System.err.println("[line $line] Error: Unterminated string.")
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
        private const val DOUBLE_QUOTES = '"'
    }

    private object Tokens {
        val DEFAULT_LEFT_PAREN = Token(type = TokenType.LEFT_PAREN, lexeme = LEFT_PAREN.toString())
        val DEFAULT_RIGHT_PAREN = Token(type = TokenType.RIGHT_PAREN, lexeme = RIGHT_PAREN.toString())
        val DEFAULT_LEFT_BRACE = Token(type = TokenType.LEFT_BRACE, lexeme = LEFT_BRACE.toString())
        val DEFAULT_RIGHT_BRACE = Token(type = TokenType.RIGHT_BRACE, lexeme = RIGHT_BRACE.toString())
        val DEFAULT_DOT = Token(type = TokenType.DOT, lexeme = DOT.toString())
        val DEFAULT_COMMA = Token(type = TokenType.COMMA, lexeme = COMMA.toString())
        val DEFAULT_STAR = Token(type = TokenType.STAR, lexeme = STAR.toString())
        val DEFAULT_EQUAL = Token(type = TokenType.EQUAL, lexeme = EQUAL.toString())
        val DEFAULT_EQUAL_EQUAL = Token(type = TokenType.EQUAL_EQUAL, lexeme = "$EQUAL$EQUAL")
        val DEFAULT_SLASH = Token(type = TokenType.SLASH, lexeme = SLASH.toString())
        val DEFAULT_PLUS = Token(type = TokenType.PLUS, lexeme = PLUS.toString())
        val DEFAULT_MINUS = Token(type = TokenType.MINUS, lexeme = MINUS.toString())
        val DEFAULT_SEMICOLON = Token(type = TokenType.SEMICOLON, lexeme = SEMICOLON.toString())
        val DEFAULT_BANG = Token(type = TokenType.BANG, lexeme = BANG.toString())
        val DEFAULT_BANG_EQUAL = Token(type = TokenType.BANG_EQUAL, lexeme = "$BANG$EQUAL")
        val DEFAULT_LESS = Token(type = TokenType.LESS, lexeme = LESS.toString())
        val DEFAULT_LESS_EQUAL = Token(type = TokenType.LESS_EQUAL, lexeme = "$LESS$EQUAL")
        val DEFAULT_GREATER = Token(type = TokenType.GREATER, lexeme = GREATER.toString())
        val DEFAULT_GREATER_EQUAL = Token(type = TokenType.GREATER_EQUAL, lexeme = "$GREATER$EQUAL")

        val DEFAULT_EOF = Token(type = TokenType.EOF, lexeme = String())
    }
}
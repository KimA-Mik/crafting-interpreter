package lexer

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
        if (!hasNext()) return Token(TokenType.EOF, lexeme = String(), line = line)
        val token = when (val c = currentChar()) {
            Symbols.LEFT_PAREN -> Token(TokenType.LEFT_PAREN, line = line, lexeme = Symbols.LEFT_PAREN.toString())
            Symbols.RIGHT_PAREN -> Token(TokenType.RIGHT_PAREN, line = line, lexeme = Symbols.RIGHT_PAREN.toString())
            Symbols.LEFT_BRACE -> Token(TokenType.LEFT_BRACE, line = line, lexeme = Symbols.LEFT_BRACE.toString())
            Symbols.RIGHT_BRACE -> Token(TokenType.RIGHT_BRACE, line = line, lexeme = Symbols.RIGHT_BRACE.toString())
            Symbols.DOT -> Token(TokenType.DOT, line = line, lexeme = Symbols.DOT.toString())
            Symbols.COMMA -> Token(TokenType.COMMA, line = line, lexeme = Symbols.COMMA.toString())
            Symbols.STAR -> Token(TokenType.STAR, line = line, lexeme = Symbols.STAR.toString())
            Symbols.SLASH -> parseSlash()
            Symbols.PLUS -> Token(TokenType.PLUS, line = line, lexeme = Symbols.PLUS.toString())
            Symbols.MINUS -> Token(TokenType.MINUS, line = line, lexeme = Symbols.MINUS.toString())
            Symbols.SEMICOLON -> Token(TokenType.SEMICOLON, line = line, lexeme = Symbols.SEMICOLON.toString())
            Symbols.EQUAL -> parseEquals()
            Symbols.BANG -> parseBang()
            Symbols.LESS -> parseLess()
            Symbols.GREATER -> parseGreater()
            Symbols.DOUBLE_QUOTES -> parseStringLiteral()
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

        val lexeme = text.substring(start, position)
        val type = KEYWORDS_TOKENS.getOrElse(lexeme) { TokenType.IDENTIFIER }

        return Token(
            type = type,
            line = line,
            lexeme = lexeme,
        )
    }

    private fun parseNumber(): Token {
        val start = position
        skipWhile { it.isDigit() }
        if (hasNext() && currentChar() == Symbols.DOT) {
            position += 1
            skipWhile { it.isDigit() }
        }

        val lexeme = text.substring(start, position)

        return Token(
            type = TokenType.NUMBER,
            lexeme = lexeme,
            line = line,
            literal = lexeme.toDoubleOrNull()?.toString()
        )
    }

    private fun parseStringLiteral(): Token {
        val start = position
        position += 1
        skipWhile { it != Symbols.DOUBLE_QUOTES }

        if (!hasNext()) {
            lexicalError = true
            reportUnterminatedString()
            return getNextToken(false)
        }

        return Token(
            type = TokenType.STRING,
            lexeme = text.substring(start, position + 1),
            line = line,
            literal = text.substring(start + 1, position),
        )
    }

    private fun parseSlash(): Token {
        val next = position + 1
        if (expectChar(next, Symbols.SLASH)) {
            skipWhile { it != '\n' }
            return getNextToken(false)
        } else {
            return Token(TokenType.SLASH, line = line, lexeme = Symbols.SLASH.toString())
        }
    }

    private fun parseEquals(): Token {
        val next = position + 1

        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            Token(TokenType.EQUAL_EQUAL, line = line, lexeme = Symbols.EQUAL_EQUAL)
        } else {
            Token(TokenType.EQUAL, line = line, lexeme = Symbols.EQUAL.toString())
        }
    }

    private fun parseBang(): Token {
        val next = position + 1

        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            Token(TokenType.BANG_EQUAL, line = line, lexeme = Symbols.BANG_EQUAL)
        } else {
            Token(TokenType.BANG, line = line, lexeme = Symbols.BANG.toString())
        }
    }

    private fun parseLess(): Token {
        val next = position + 1
        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            Token(TokenType.LESS_EQUAL, line = line, lexeme = Symbols.LESS_EQUAL)
        } else {
            Token(TokenType.LESS, line = line, lexeme = Symbols.LESS.toString())
        }
    }

    private fun parseGreater(): Token {
        val next = position + 1
        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            Token(TokenType.GREATER_EQUAL, line = line, lexeme = Symbols.GREATER_EQUAL)
        } else {
            Token(TokenType.GREATER, line = line, lexeme = Symbols.GREATER.toString())
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
        private val KEYWORDS_TOKENS = mapOf(
            Keywords.AND to TokenType.AND,
            Keywords.CLASS to TokenType.CLASS,
            Keywords.ELSE to TokenType.ELSE,
            Keywords.FALSE to TokenType.FALSE,
            Keywords.FOR to TokenType.FOR,
            Keywords.FUN to TokenType.FUN,
            Keywords.IF to TokenType.IF,
            Keywords.NIL to TokenType.NIL,
            Keywords.OR to TokenType.OR,
            Keywords.PRINT to TokenType.PRINT,
            Keywords.RETURN to TokenType.RETURN,
            Keywords.SUPER to TokenType.SUPER,
            Keywords.THIS to TokenType.THIS,
            Keywords.TRUE to TokenType.TRUE,
            Keywords.VAR to TokenType.VAR,
            Keywords.WHILE to TokenType.WHILE,
        )
    }
}
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
        if (!hasNext()) return DefaultTokens.EOF
        val token = when (val c = currentChar()) {
            Symbols.LEFT_PAREN -> DefaultTokens.LEFT_PAREN
            Symbols.RIGHT_PAREN -> DefaultTokens.RIGHT_PAREN
            Symbols.LEFT_BRACE -> DefaultTokens.LEFT_BRACE
            Symbols.RIGHT_BRACE -> DefaultTokens.RIGHT_BRACE
            Symbols.DOT -> DefaultTokens.DOT
            Symbols.COMMA -> DefaultTokens.COMMA
            Symbols.STAR -> DefaultTokens.STAR
            Symbols.SLASH -> parseSlash()
            Symbols.PLUS -> DefaultTokens.PLUS
            Symbols.MINUS -> DefaultTokens.MINUS
            Symbols.SEMICOLON -> DefaultTokens.SEMICOLON
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
        return KEYWORDS_TOKENS.getOrElse(lexeme) {
            Token(
                type = TokenType.IDENTIFIER,
                lexeme = lexeme,
            )
        }
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
            literal = text.substring(start + 1, position),
        )
    }

    private fun parseSlash(): Token {
        val next = position + 1
        if (expectChar(next, Symbols.SLASH)) {
            skipWhile { it != '\n' }
            return getNextToken(false)
        } else {
            return DefaultTokens.SLASH
        }
    }

    private fun parseEquals(): Token {
        val next = position + 1

        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            DefaultTokens.EQUAL_EQUAL
        } else {
            DefaultTokens.EQUAL
        }
    }

    private fun parseBang(): Token {
        val next = position + 1

        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            DefaultTokens.BANG_EQUAL
        } else {
            DefaultTokens.BANG
        }
    }

    private fun parseLess(): Token {
        val next = position + 1
        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            DefaultTokens.LESS_EQUAL
        } else {
            DefaultTokens.LESS
        }
    }

    private fun parseGreater(): Token {
        val next = position + 1
        return if (expectChar(next, Symbols.EQUAL)) {
            position += 1
            DefaultTokens.GREATER_EQUAL
        } else {
            DefaultTokens.GREATER
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
            Keywords.AND to DefaultTokens.AND,
            Keywords.CLASS to DefaultTokens.CLASS,
            Keywords.ELSE to DefaultTokens.ELSE,
            Keywords.FALSE to DefaultTokens.FALSE,
            Keywords.FOR to DefaultTokens.FOR,
            Keywords.FUN to DefaultTokens.FUN,
            Keywords.IF to DefaultTokens.IF,
            Keywords.NIL to DefaultTokens.NIL,
            Keywords.OR to DefaultTokens.OR,
            Keywords.PRINT to DefaultTokens.PRINT,
            Keywords.RETURN to DefaultTokens.RETURN,
            Keywords.SUPER to DefaultTokens.SUPER,
            Keywords.THIS to DefaultTokens.THIS,
            Keywords.TRUE to DefaultTokens.TRUE,
            Keywords.VAR to DefaultTokens.VAR,
            Keywords.WHILE to DefaultTokens.WHILE,
        )
    }
}
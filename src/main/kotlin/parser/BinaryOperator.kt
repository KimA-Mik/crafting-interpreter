package parser

import lexer.Symbols
import lexer.TokenType

enum class BinaryOperator {
    STAR, SLASH, PLUS, MINUS,
    EQUAL_EQUAL, BANG_EQUAL,
    LESS, LESS_EQUAL,
    GREATER, GREATER_EQUAL;

    override fun toString(): String {
        return when (this) {
            STAR -> Symbols.STAR.toString()
            SLASH -> Symbols.SLASH.toString()
            PLUS -> Symbols.PLUS.toString()
            MINUS -> Symbols.MINUS.toString()
            EQUAL_EQUAL -> Symbols.EQUAL_EQUAL
            BANG_EQUAL -> Symbols.BANG_EQUAL
            LESS -> Symbols.LESS.toString()
            LESS_EQUAL -> Symbols.LESS_EQUAL
            GREATER -> Symbols.GREATER.toString()
            GREATER_EQUAL -> Symbols.GREATER_EQUAL
        }
    }

    companion object {
        fun fromToken(tokenType: TokenType): BinaryOperator? {
            return when (tokenType) {
                TokenType.STAR -> STAR
                TokenType.SLASH -> SLASH
                TokenType.PLUS -> PLUS
                TokenType.MINUS -> MINUS
                TokenType.EQUAL_EQUAL -> EQUAL_EQUAL
                TokenType.BANG_EQUAL -> BANG_EQUAL
                TokenType.LESS -> LESS
                TokenType.LESS_EQUAL -> LESS_EQUAL
                TokenType.GREATER -> GREATER
                TokenType.GREATER_EQUAL -> GREATER_EQUAL
                else -> null
            }
        }
    }
}
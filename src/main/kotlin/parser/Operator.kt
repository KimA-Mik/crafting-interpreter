package parser

import lexer.DefaultTokens

enum class Operator {
    STAR, SLASH, PLUS, MINUS,
    EQUAL_EQUAL, BANG_EQUAL,
    LESS, LESS_EQUAL,
    GREATER, GREATER_EQUAL;

    override fun toString(): String {
        return when (this) {
            STAR -> DefaultTokens.STAR.lexeme
            SLASH -> DefaultTokens.SLASH.lexeme
            PLUS -> DefaultTokens.PLUS.lexeme
            MINUS -> DefaultTokens.MINUS.lexeme
            EQUAL_EQUAL -> DefaultTokens.EQUAL_EQUAL.lexeme
            BANG_EQUAL -> DefaultTokens.BANG_EQUAL.lexeme
            LESS -> DefaultTokens.LESS.lexeme
            LESS_EQUAL -> DefaultTokens.LESS_EQUAL.lexeme
            GREATER -> DefaultTokens.GREATER.lexeme
            GREATER_EQUAL -> DefaultTokens.GREATER_EQUAL.lexeme
        }
    }
}
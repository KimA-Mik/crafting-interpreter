package parser

import lexer.DefaultTokens
import lexer.TokenType

enum class UnaryOperator {
    BANG, MINUS;

    override fun toString(): String {
        return when (this) {
            BANG -> DefaultTokens.BANG.lexeme
            MINUS -> DefaultTokens.MINUS.lexeme
        }
    }

    companion object {
        fun fromToken(tokenType: TokenType): UnaryOperator? {
            return when (tokenType) {
                TokenType.BANG -> BANG
                TokenType.MINUS -> MINUS
                else -> null
            }
        }
    }
}
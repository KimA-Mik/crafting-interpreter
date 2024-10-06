package parser

import lexer.Symbols
import lexer.TokenType

enum class UnaryOperator {
    BANG, MINUS;

    override fun toString(): String {
        return when (this) {
            BANG -> Symbols.BANG.toString()
            MINUS -> Symbols.MINUS.toString()
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
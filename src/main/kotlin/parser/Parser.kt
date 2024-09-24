package parser

import lexer.Token
import lexer.TokenType

class Parser(private val tokens: List<Token>) {
    fun parse(): List<Expression> {
        val ast = mutableListOf<Expression>()

        while (hasNext()) {
            val expression = getNextExpression()
            if (expression != null) {
                ast.add(expression)
            } else {
                break
            }
        }

        return ast
    }

    private var position = 0

    private fun getCurrent() = tokens.getOrNull(position++)
    private fun hasNext() = position < tokens.size

    private fun getNextExpression(): Expression? {
        val token = getCurrent()
        val expression = when (token?.type) {
            TokenType.FALSE -> Expression.Literal.FalseLiteral
            TokenType.NIL -> Expression.Literal.NilLiteral
            TokenType.TRUE -> Expression.Literal.TrueLiteral
            TokenType.NUMBER -> parseNumberLiteral(token)
            TokenType.STRING -> parseStringLiteral(token)
            TokenType.LEFT_PAREN -> parseGroup()
            TokenType.RIGHT_PAREN -> null
            else -> {
                null
            }
        }

        return expression
    }

    private fun parseNumberLiteral(token: Token): Expression.Literal.NumberLiteral? {
        val number = token.literal?.toDoubleOrNull() ?: return null
        return Expression.Literal.NumberLiteral(number)
    }

    private fun parseStringLiteral(token: Token): Expression.Literal.StringLiteral? {
        val string = token.literal ?: return null
        return Expression.Literal.StringLiteral(string)
    }

    private fun parseGroup(): Expression {
        val group = mutableListOf<Expression>()

        while (hasNext()) {
            val expression = getNextExpression()
            if (expression != null) {
                group.add(expression)
            } else {
                break
            }
        }

        return Expression.Grouping(group)
    }
}
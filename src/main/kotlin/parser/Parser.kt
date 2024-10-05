package parser

import lexer.Token
import lexer.TokenType


class Parser(private val tokens: List<Token>) {
    fun parse(): List<Expression> {
        val res = mutableListOf<Expression>()

        try {
            expression()?.let { res.add(it) }
        } catch (e: Exception) {
            println(e.message)
        }

        return res
    }

    private var position = 0
    private fun peek() = tokens[position]
    private fun previous() = tokens[position - 1]
    private fun isEnd() = peek().type == TokenType.EOF

    private fun advance(): Token {
        if (!isEnd()) position++
        return tokens[position]
    }

    private fun consume(token: TokenType, msg: String) {
        if (!match(token))
            throw Exception(msg)
    }

    private fun check(type: TokenType): Boolean {
        if (isEnd()) return false
        return peek().type == type
    }

    private fun match(vararg tokenTypes: TokenType): Boolean {
        for (tokenType in tokenTypes) {
            if (check(tokenType)) {
                advance()
                return true
            }
        }
        return false
    }


    private fun expression(): Expression? {
        return equality()
    }

    private fun equality(): Expression? {
        var expression = comparison() ?: return null

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: return null
            val right = comparison() ?: return null
            expression = Expression.Binary(expression, operator, right)
        }

        return expression
    }

    private fun comparison(): Expression? {
        var expression = term() ?: return null

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: return null
            val right = term() ?: return null
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun term(): Expression? {
        var expression = factor() ?: return null

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: return null
            val right = factor() ?: return null
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun factor(): Expression? {
        var expression = unary() ?: return null

        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: return null
            val right = unary() ?: return null
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun unary(): Expression? {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = UnaryOperator.fromToken(previous().type) ?: return null
            val right = unary() ?: return null
            return Expression.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expression? {
        if (match(TokenType.FALSE)) return Expression.Literal.FalseLiteral
        if (match(TokenType.TRUE)) return Expression.Literal.TrueLiteral
        if (match(TokenType.NIL)) return Expression.Literal.NilLiteral

        if (match(TokenType.STRING)) {
            val string = previous().literal ?: return null
            return Expression.Literal.StringLiteral(string)
        }

        if (match(TokenType.NUMBER)) {
            val number = previous().literal?.toDoubleOrNull() ?: return null
            return Expression.Literal.NumberLiteral(number)
        }


        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression() ?: return null
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expression.Grouping(expr)
        }

        return null
    }
}
package parser

import lexer.Token
import lexer.TokenType


class Parser(private val tokens: List<Token>) {
    fun parseExpression(): Expression? {
        try {
            return expression()
        } catch (e: ParserException) {
            handleParserException(e)
        } catch (e: Exception) {
            System.err.println("Unknown exception ${e.message}")
        }

        return null
    }

    fun parseStatements(): List<Statement> {
        val statements = mutableListOf<Statement>()
        try {
            while (!isEnd()) {
                statements.add(statement())
            }
        } catch (e: ParserException) {
            handleParserException(e)
        } catch (e: Exception) {
            System.err.println("Unknown exception ${e.message}")
        }

        return statements
    }

    private fun handleParserException(e: ParserException) {
        val token = e.token
        when (token.type) {
            TokenType.EOF -> System.err.println("[line ${token.line}] Error at end: ${e.message}")
            else -> System.err.println("[line ${token.line}] Error at '${token.lexeme}': ${e.message}")
        }
    }

    var parserError = false
        private set

    class ParserException(val token: Token, message: String) : Exception(message)

    private var position = 0
    private fun peek() = tokens[position]
    private fun previous() = tokens[position - 1]
    private fun isEnd() = peek().type == TokenType.EOF

    private fun advance(): Token {
        if (!isEnd()) position++
        return previous()
    }

    private fun consume(token: TokenType, msg: String): Token {
        if (check(token)) return advance()

        throw error(peek(), msg)
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

    private fun error(token: Token, message: String): ParserException {
        parserError = true
        return ParserException(token, message)
    }

    private fun expression(): Expression {
        return assigment()
    }

    private fun assigment(): Expression {
        val expression = equality()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assigment()

            if (expression is Expression.Variable) {
                val name = expression.name
                return Expression.Assigment(name, value)
            }

            throw error(equals, "Invalid assignment target.")
        }

        return expression
    }

    private fun equality(): Expression {
        var expression = comparison()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: throw error(peek(), "Expect binary operator.")
            val right = comparison()
            expression = Expression.Binary(expression, operator, right)
        }

        return expression
    }

    private fun comparison(): Expression {
        var expression = term()

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: throw error(peek(), "Expect binary operator.")
            val right = term()
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun term(): Expression {
        var expression = factor()

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: throw error(peek(), "Expect binary operator.")
            val right = factor()
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun factor(): Expression {
        var expression = unary()

        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = BinaryOperator.fromToken(previous().type) ?: throw error(peek(), "Expect binary operator.")
            val right = unary()
            expression = Expression.Binary(expression, operator, right)
        }
        return expression
    }

    private fun unary(): Expression {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = UnaryOperator.fromToken(previous().type) ?: throw error(peek(), "Expect unary operator.")
            val right = unary()
            return Expression.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expression {
        if (match(TokenType.FALSE)) return Expression.Literal.FalseLiteral
        if (match(TokenType.TRUE)) return Expression.Literal.TrueLiteral
        if (match(TokenType.NIL)) return Expression.Literal.NilLiteral

        if (match(TokenType.STRING)) {
            val string = previous().literal ?: throw error(peek(), "Expect string literal.")
            return Expression.Literal.StringLiteral(string)
        }

        if (match(TokenType.NUMBER)) {
            val number = previous().literal?.toDoubleOrNull() ?: throw error(peek(), "Expect number literal.")
            return Expression.Literal.NumberLiteral(number)
        }


        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expression.Grouping(expr)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expression.Variable(previous())
        }

        throw error(peek(), "Expect expression.")
    }

    private fun statement(): Statement {
        return when {
            match(TokenType.PRINT) -> printStatement()
            match(TokenType.LEFT_BRACE) -> block()
            match(TokenType.VAR) -> variableDeclaration()
            else -> expressionStatement()
        }
    }

    private fun block(): Statement.Block {
        val statements = mutableListOf<Statement>()
        while (!check(TokenType.RIGHT_BRACE) && !isEnd()) {
            statements.add(statement())
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return Statement.Block(statements)
    }


    private fun variableDeclaration(): Statement.Declaration.Variable {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")

        var initializer: Expression? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Statement.Declaration.Variable(name, initializer)
    }

    private fun printStatement(): Statement.Print {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Statement.Print(value)
    }

    private fun expressionStatement(): Statement.Expr {
        val expression = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Statement.Expr(expression)
    }
}
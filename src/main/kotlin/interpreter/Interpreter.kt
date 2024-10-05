package interpreter

import parser.Expression

class Interpreter {
    fun evaluate(expression: Expression): Any? {
        return when (expression) {
            is Expression.Binary -> TODO()
            is Expression.Grouping -> TODO()
            is Expression.Literal -> evaluateLiteral(expression)
            is Expression.Unary -> TODO()
        }
    }

    private fun evaluateLiteral(expression: Expression.Literal): Any? {
        return when (expression) {
            Expression.Literal.FalseLiteral -> false
            Expression.Literal.NilLiteral -> null
            is Expression.Literal.NumberLiteral -> TODO()
            is Expression.Literal.StringLiteral -> TODO()
            Expression.Literal.TrueLiteral -> true
        }

    }
}
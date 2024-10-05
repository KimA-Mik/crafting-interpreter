package interpreter

import parser.Expression

class Interpreter {
    fun evaluate(expression: Expression) = EvaluationResult(evaluateExpression(expression))

    private fun evaluateExpression(expression: Expression): Any? {
        return when (expression) {
            is Expression.Binary -> TODO()
            is Expression.Grouping -> evaluateGroupingExpression(expression)
            is Expression.Literal -> evaluateLiteral(expression)
            is Expression.Unary -> TODO()
        }
    }

    private fun evaluateLiteral(expression: Expression.Literal): Any? {
        return when (expression) {
            Expression.Literal.FalseLiteral -> false
            Expression.Literal.NilLiteral -> null
            is Expression.Literal.NumberLiteral -> expression.value
            is Expression.Literal.StringLiteral -> expression.value
            Expression.Literal.TrueLiteral -> true
        }
    }

    private fun evaluateGroupingExpression(expression: Expression.Grouping): Any {
        return evaluate(expression.expressions)
    }
}
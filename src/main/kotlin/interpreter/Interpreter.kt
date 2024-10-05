package interpreter

import parser.Expression
import parser.UnaryOperator

class Interpreter {
    fun evaluate(expression: Expression) = EvaluationResult(evaluateExpression(expression))

    private fun evaluateExpression(expression: Expression): Any? {
        return when (expression) {
            is Expression.Binary -> TODO()
            is Expression.Grouping -> evaluateGroupingExpression(expression)
            is Expression.Literal -> evaluateLiteral(expression)
            is Expression.Unary -> evaluateUnaryExpression(expression)
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

    private fun evaluateGroupingExpression(expression: Expression.Grouping): Any? {
        return evaluateExpression(expression.expressions)
    }

    private fun evaluateUnaryExpression(expression: Expression.Unary): Any {
        val right = evaluateExpression(expression.expression)
        return when (expression.unaryOperator) {
            UnaryOperator.BANG -> !isTruthy(right)
            UnaryOperator.MINUS -> -(right as Double)
        }
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }
}
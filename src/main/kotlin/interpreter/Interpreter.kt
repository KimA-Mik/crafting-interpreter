package interpreter

import parser.BinaryOperator
import parser.Expression
import parser.Operator
import parser.UnaryOperator

class Interpreter {
    class RuntimeError(val operator: Operator, message: String) : Exception(message)

    fun evaluate(expression: Expression): EvaluationResult? {
        try {
            return EvaluationResult(evaluateExpression(expression))
        } catch (e: RuntimeError) {
            System.err.println(e.message)
        }
        return null
    }

    var evaluationError = false
        private set

    private fun evaluateExpression(expression: Expression): Any? {
        return when (expression) {
            is Expression.Binary -> evaluateBinaryExpression(expression)
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
            UnaryOperator.MINUS -> {
                checkNumberOperand(expression.unaryOperator, right)
                -(right as Double)
            }
        }
    }

    private fun evaluateBinaryExpression(expression: Expression.Binary): Any? {
        val left = evaluateExpression(expression.left)
        val right = evaluateExpression(expression.right)
        return when (expression.operator) {
            BinaryOperator.STAR -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) * (right as Double)
            }

            BinaryOperator.SLASH -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) / (right as Double)
            }

            BinaryOperator.MINUS -> (left as Double) - (right as Double)
            BinaryOperator.PLUS -> {
                if (left is Double && right is Double)
                    left + right
                else if (left is String && right is String)
                    left + right
                else
                    null
            }

            BinaryOperator.EQUAL_EQUAL -> isEqual(left, right)
            BinaryOperator.BANG_EQUAL -> !isEqual(left, right)
            BinaryOperator.LESS -> (left as Double) < (right as Double)
            BinaryOperator.LESS_EQUAL -> (left as Double) <= (right as Double)
            BinaryOperator.GREATER -> (left as Double) > (right as Double)
            BinaryOperator.GREATER_EQUAL -> (left as Double) >= (right as Double)
        }
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false

        return a == b
    }

    private fun checkNumberOperand(unaryOperator: UnaryOperator, right: Any?) {
        if (right is Double) return
        evaluationError = true
        throw RuntimeError(unaryOperator, "Operand must be a number.")
    }

    private fun checkNumberOperands(unaryOperator: BinaryOperator, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        evaluationError = true
        throw RuntimeError(unaryOperator, "Operands must be numbers.")
    }
}
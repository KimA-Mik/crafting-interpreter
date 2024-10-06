package interpreter

import parser.*

class Interpreter {
    class RuntimeError(val operator: Operator, message: String) : Exception(message)

    fun evaluate(statements: List<Statement>) {
        try {
            for (statement in statements) {
                evaluateStatement(statement)
            }
        } catch (e: RuntimeError) {
            System.err.println(e.message)
        }
    }

    private fun evaluateStatement(statement: Statement) {
        when (statement) {
            is Statement.Expr -> evaluateExprStatement(statement)
            is Statement.Print -> evaluatePrintStatement(statement)
        }
    }

    private fun evaluateExprStatement(statement: Statement.Expr) {
        evaluateExpression(statement.expression)
    }

    private fun evaluatePrintStatement(statement: Statement.Print) {
        val res = evaluateExpression(statement.expression)
        println(res.stringify())
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

    private fun evaluateBinaryExpression(expression: Expression.Binary): Any {
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

            BinaryOperator.MINUS -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) - (right as Double)
            }

            BinaryOperator.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    plusOperatorError()
                }
            }

            BinaryOperator.EQUAL_EQUAL -> isEqual(left, right)
            BinaryOperator.BANG_EQUAL -> !isEqual(left, right)
            BinaryOperator.LESS -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) < (right as Double)
            }

            BinaryOperator.LESS_EQUAL -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) <= (right as Double)
            }

            BinaryOperator.GREATER -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) > (right as Double)
            }

            BinaryOperator.GREATER_EQUAL -> {
                checkNumberOperands(expression.operator, left, right)
                (left as Double) >= (right as Double)
            }
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

    private fun plusOperatorError() {
        evaluationError = true
        throw RuntimeError(BinaryOperator.PLUS, "Operands must be two numbers or two strings.")
    }
}
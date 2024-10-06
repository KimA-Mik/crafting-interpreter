package interpreter

import parser.BinaryOperator
import parser.Declaration
import parser.Expression
import parser.UnaryOperator

class Interpreter {
    private val environment = Environment()
    var evaluationError = false
        private set

    class RuntimeError(message: String) : Exception(message)

    fun interpretExpression(expression: Expression): EvaluationResult? {
        try {
            return EvaluationResult(evaluateExpression(expression))
        } catch (e: RuntimeError) {
            System.err.println(e.message)
            evaluationError = true
        }
        return null
    }

    fun execute(declarations: List<Declaration>) {
        try {
            for (declaration in declarations) {
                executeDeclaration(declaration)
            }
        } catch (e: RuntimeError) {
            System.err.println(e.message)
            evaluationError = true
        }
    }

    private fun executeDeclaration(declaration: Declaration) {
        when (declaration) {
            is Declaration.Statement -> executeStatement(declaration)
            is Declaration.Variable -> executeVariable(declaration)
        }
    }

    private fun executeStatement(statement: Declaration.Statement) {
        when (statement) {
            is Declaration.Statement.Expr -> executeExprStatement(statement)
            is Declaration.Statement.Print -> executePrintStatement(statement)
        }
    }

    private fun executeVariable(variable: Declaration.Variable) {
        var value: Any? = null
        variable.initializer?.let {
            value = evaluateExpression(it)
        }

        environment.define(variable.name.lexeme, value)
    }

    private fun executeExprStatement(statement: Declaration.Statement.Expr) {
        evaluateExpression(statement.expression)
    }

    private fun executePrintStatement(statement: Declaration.Statement.Print) {
        val res = evaluateExpression(statement.expression)
        println(res.stringify())
    }

    private fun evaluateExpression(expression: Expression): Any? {
        return when (expression) {
            is Expression.Binary -> evaluateBinaryExpression(expression)
            is Expression.Grouping -> evaluateGroupingExpression(expression)
            is Expression.Literal -> evaluateLiteral(expression)
            is Expression.Unary -> evaluateUnaryExpression(expression)
            is Expression.Variable -> evaluateVariable(expression)
        }
    }

    private fun evaluateVariable(variable: Expression.Variable): Any? {
        return environment.get(variable.name)
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
                checkNumberOperand(right)
                -(right as Double)
            }
        }
    }

    private fun evaluateBinaryExpression(expression: Expression.Binary): Any {
        val left = evaluateExpression(expression.left)
        val right = evaluateExpression(expression.right)
        return when (expression.operator) {
            BinaryOperator.STAR -> {
                checkNumberOperands(left, right)
                (left as Double) * (right as Double)
            }

            BinaryOperator.SLASH -> {
                checkNumberOperands(left, right)
                (left as Double) / (right as Double)
            }

            BinaryOperator.MINUS -> {
                checkNumberOperands(left, right)
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
                checkNumberOperands(left, right)
                (left as Double) < (right as Double)
            }

            BinaryOperator.LESS_EQUAL -> {
                checkNumberOperands(left, right)
                (left as Double) <= (right as Double)
            }

            BinaryOperator.GREATER -> {
                checkNumberOperands(left, right)
                (left as Double) > (right as Double)
            }

            BinaryOperator.GREATER_EQUAL -> {
                checkNumberOperands(left, right)
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

    private fun checkNumberOperand(right: Any?) {
        if (right is Double) return
        throw RuntimeError("Operand must be a number.")
    }

    private fun checkNumberOperands(left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError("Operands must be numbers.")
    }

    private fun plusOperatorError() {
        throw RuntimeError("Operands must be two numbers or two strings.")
    }
}
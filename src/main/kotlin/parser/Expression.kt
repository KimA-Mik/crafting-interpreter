package parser

sealed interface Expression {
    sealed interface Literal : Expression {
        data class StringLiteral(val value: String) : Literal {
            override fun toString(): String = value
        }

        data class NumberLiteral(val value: Double) : Literal {
            override fun toString(): String = value.toString()
        }

        data object TrueLiteral : Literal {
            override fun toString() = "true"
        }

        data object FalseLiteral : Literal {
            override fun toString() = "false"
        }

        data object NilLiteral : Literal {
            override fun toString() = "nil"
        }
    }

    data class Unary(val operator: Operator, val expression: Expression) : Expression
    data class Binary(val left: Expression, val operator: Operator, val right: Expression) : Expression
    data class Grouping(val expressions: List<Expression>) : Expression {
        override fun toString(): String {
            val sb = StringBuilder()
            expressions.forEachIndexed { index, it ->
                if (index > 0) sb.append(' ')
                sb.append(it)
            }
            return "(group $sb)"
        }
    }
}
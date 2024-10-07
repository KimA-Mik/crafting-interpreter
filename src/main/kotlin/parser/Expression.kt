package parser

import lexer.Token

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

    data class Unary(val unaryOperator: UnaryOperator, val expression: Expression) : Expression {
        override fun toString(): String = "($unaryOperator $expression)"
    }

    data class Binary(val left: Expression, val operator: BinaryOperator, val right: Expression) : Expression {
        override fun toString(): String = "($operator $left $right)"
    }

    data class Grouping(val expressions: Expression) : Expression {
        override fun toString() = "(group $expressions)"
    }

    data class Variable(val name: Token) : Expression
    data class Assigment(val name: Token, val expression: Expression) : Expression
}
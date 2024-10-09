package parser

import lexer.Token

sealed interface Statement {
    sealed interface Declaration : Statement {
        data class Variable(val name: Token, val initializer: Expression?) : Declaration
    }

    data class Expr(val expression: Expression) : Statement
    data class Print(val expression: Expression) : Statement
    data class Block(val statements: List<Statement>) : Statement
}



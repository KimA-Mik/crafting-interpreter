package parser

import lexer.Token

sealed interface Declaration {
    data class Variable(val name: Token, val initializer: Expression?) : Declaration
    sealed interface Statement : Declaration {
        data class Print(val expression: Expression) : Statement
        data class Expr(val expression: Expression) : Statement
    }
}
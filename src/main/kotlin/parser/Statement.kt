package parser

sealed interface Statement {
    data class Print(val expression: Expression) : Statement
    data class Expr(val expression: Expression) : Statement
}
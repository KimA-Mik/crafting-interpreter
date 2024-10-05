package lexer

data class Token(
    val type: TokenType,
    val lexeme: String,
    val line: Int,
    val literal: String? = null,
) {
    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}
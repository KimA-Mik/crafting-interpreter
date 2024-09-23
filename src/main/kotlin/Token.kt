data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: String? = null,
) {
    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}
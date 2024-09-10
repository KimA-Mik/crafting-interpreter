data class Token(
    val type: TokenType,
    val string: String,
    val data: Any? = null,
) {
    override fun toString(): String {
        return "$type $string $data"
    }
}
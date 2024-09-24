package lexer

fun Char.isIdentifierStart() = this.isLetter() || this == '_'

fun Char.isIdentifier() = this.isIdentifierStart() || this.isDigit()
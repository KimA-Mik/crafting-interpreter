package interpreter

import lexer.Token

class Environment {
    private val values = mutableMapOf<String, Any?>()

    fun define(key: String, value: Any?) {
        values[key] = value
    }

    fun assign(key: String, value: Any?) {
        if (values.containsKey(key)) {
            values[key] = value
            return
        }

        throw Interpreter.RuntimeError("Undefined variable \"$key\".")
    }

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        throw Interpreter.RuntimeError("Undefined variable \"${name.lexeme}\".")
    }
}
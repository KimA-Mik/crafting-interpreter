package interpreter

@JvmInline
value class EvaluationResult(private val result: Any?) {
    override fun toString() = result.stringify()
}
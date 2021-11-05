package calculator

fun main() {
    val calc = Calc()
    while (calc.work) {
        val input = readLine()!!.trim()
        calc.parse(input)
    }
}

package calculator

import java.math.BigInteger

class Calc {
    // Regexp
    private val regUnknownCommand = "/\\w*".toRegex()
    private val regVariableAssign = "[a-zA-Z]+\\s*=\\s*-*\\w+\\s*".toRegex()
    private val regInvVariable = "\\w+\\s*=\\s*-*\\w+".toRegex()
    private val regInvEquals = "=".toRegex()
    private val regVar ="[a-zA-Z]+".toRegex()
    private val varMap = mutableMapOf<String, BigInteger>()
    var work = true

    fun parse(input: String) {
        when {
            input == "/exit" -> exit()
            input == "/help" -> help()
            input.matches(regUnknownCommand) -> unknownCommand()
            input == "" -> empty()
            input.toBigIntegerOrNull() != null -> println(input)
            input.matches(regVariableAssign) -> varAdd(input)
            input.matches(regInvVariable) -> invVariable()
            regInvEquals.find(input) != null -> invEquals()
            input.matches(regVar) -> varInf(input)
            else -> parseExpression(input)
        }
    }

    private fun parseExpression(input: String) {
        var expr = input
        for (k in varMap.keys) expr = expr.replace(k, varMap[k].toString())

        expr = expr.replace(" ", "").replace("--","+").replace("\\++".toRegex(), "+").replace("+-", "-")
        expr = expr.replace("+", " + ").replace("-", " - ").replace("*", " * ").replace("/", " / ")
        expr = expr.replace("^", " ^ ").replace("(", " ( ").replace(")", " ) ")
        expr = expr.trim()

        try {
            val postfix = infToPostfix(expr)
            if (postfix.contains("(") || postfix.contains(")")) println("Invalid expression")
            else calculate(postfix)
        } catch (e: Exception) {
            println("Invalid expression")
        }
    }

    private fun calculate(list: MutableList<String>) {
        val stack = mutableListOf<String>()
        for (element in list) {
            when {
                element.toBigIntegerOrNull() != null -> stack.add(element) // 1
                else -> {
                    when (element) {
                        "+" -> {
                            val result = stack[stack.lastIndex - 1].toBigInteger() + stack.last().toBigInteger()
                            stack.removeLast()
                            stack.removeLast()
                            stack.add(result.toString())
                        }
                        "-" -> {
                            val result = stack[stack.lastIndex - 1].toBigInteger() - stack.last().toBigInteger()
                            stack.removeLast()
                            stack.removeLast()
                            stack.add(result.toString())
                        }
                        "*" -> {
                            val result = stack[stack.lastIndex - 1].toBigInteger() * stack.last().toBigInteger()
                            stack.removeLast()
                            stack.removeLast()
                            stack.add(result.toString())
                        }
                        "/" -> {
                            val result = stack[stack.lastIndex - 1].toBigInteger() / stack.last().toBigInteger()
                            stack.removeLast()
                            stack.removeLast()
                            stack.add(result.toString())
                        }
                        "^" -> {
                            val result = stack[stack.lastIndex - 1].toBigInteger().pow(stack.last().toInt())
                            stack.removeLast()
                            stack.removeLast()
                            stack.add(result.toString())
                        }
                    }
                }

            }
        }
        println(stack[0])

    }

    private fun varInf(input: String) {
        if (varMap[input] != null) println(varMap[input]) else println("Unknown variable")
    }

    private fun invEquals() {
        println("Invalid assignment")
    }

    private fun invVariable() {
        println("Invalid identifier")
    }

    private fun varAdd(input: String) {
        val (a, b) = input.split("=").map { it.trim() }
        try { varMap[a] = b.toBigInteger() }
        catch (e: Exception) {
            if (varMap.contains(b)) {
                varMap[a] = varMap[b]!!
            } else {
                if (!b.matches("[a-zA-Z]+".toRegex())) {
                    println("Invalid assignment")
                } else {
                println("Unknown variable")
                }
            }
        }
    }

    private fun unknownCommand() {
        println("Unknown command")
    }

    private fun empty() { }

    private fun help() {
        println("The program calculates the sum of numbers. It supports both several unary and binary minus operators.")
    }

    private fun exit() {
        work = false
        println("Bye!")
    }

    private fun infToPostfix(infix: String): MutableList<String> {
        val stack = mutableListOf<String>()
        val result = mutableListOf<String>()
        val input = infix.split("\\s+".toRegex()).toMutableList()
        val opMap = mutableMapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3)

        for (element in input) {
            when {
                element.toBigIntegerOrNull() != null -> result.add(element) // 1
                stack.isEmpty() || stack.last() == "(" -> stack.add(element) // 2
                element == "(" -> stack.add(element) // 5
                element == ")" -> { // 6
                    while (stack.last() != "(") {
                        result.add(stack.last())
                        stack.removeLast()
                    }
                    stack.removeLast()
                }
                opMap[element]!! > opMap[stack.last()]!! -> stack.add(element) // 3
                opMap[element]!! <= opMap[stack.last()]!! -> { // 4
                    while (stack.last() != "(" || if (stack.last() == "(") false else opMap[stack.last()]!! <= opMap[element]!!) {
                        result.add(stack.last())
                        stack.removeLast()
                        if (stack.isEmpty()) break
                    }
                    stack.add(element)
                }
            }
        }
        while (stack.isNotEmpty()) { // 7
            result.add(stack.last())
            stack.removeLast()
        }
        return result
    }

}
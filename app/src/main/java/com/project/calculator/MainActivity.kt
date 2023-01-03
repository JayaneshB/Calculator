package com.project.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.project.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = false

    private lateinit var binding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun numberAction(view: View) {

        if (view is Button) {

            if (view.text == ".") {

                if (canAddDecimal)
                    binding.calculatingArea.append(view.text)
                canAddDecimal = false
            } else {

                binding.calculatingArea.append(view.text)
                canAddOperation = true
            }
        }
    }

    fun operatorAction (view:View) {

        if(view is Button && canAddOperation) {

            binding.calculatingArea.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {

        binding.calculatingArea.text=""
        binding.resultArea.text=""

        binding.resultArea.visibility = View.GONE

    }

    fun backspaceAction(view: View) {

        val length = binding.calculatingArea.length()
        if(length>0) {
            binding.calculatingArea.text = binding.calculatingArea.text.subSequence(0,length-1)
        }
    }

    fun equalAction(view : View) {

        try {
            val result = calculateResult()
            binding.resultArea.text = result
            binding.resultArea.visibility = View.VISIBLE
        } catch (e: Exception) {
            binding.resultArea.text = "Error"
            binding.resultArea.visibility = View.VISIBLE
        }

    }

    private fun calculateResult(): String {

        val digitOperator = digitOperators()
        if(digitOperator.isEmpty())
            return ""
         
        val timeDivision = timeDivision(digitOperator)
        if(timeDivision.isEmpty())
            return ""

        val result = addSubtractCalculate(timeDivision)

        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {

        var result = passedList[0] as Float

        for(i in passedList.indices) {

            if(passedList[i] is Char && i != passedList.lastIndex) {

                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float

                if(operator == '+')
                    result += nextDigit
                if(operator == '-')
                    result -= nextDigit

            }

        }
        return result

    }

    private fun timeDivision(passedList : MutableList<Any>): MutableList<Any>  {

        var list = passedList

        while(list.contains('X') || list.contains('/') || list.contains('%')) {

            list = calcTimesDiv(list)
        }

        return list
    }

    private fun calcTimesDiv(passedList : MutableList<Any>): MutableList<Any> {

        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices) {

            if(passedList[i] is Char && i != passedList.lastIndex  && i < restartIndex) {

                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1 ] as Float

                when(operator) {

                    'X' -> {

                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '/' -> {

                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    '%' -> {

                        newList.add(prevDigit % nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {

                        newList.add(prevDigit)
                        newList.add(operator)

                    }
                }
            }

            if( i > restartIndex) {

                newList.add(passedList[i])
            }
        }
        return newList

    }

//    private fun digitOperators() : MutableList<Any> {
//
//        val list = mutableListOf<Any>()
//
//        var currDigit = ""
//
//        for(character in binding.calculatingArea.text ) {
//
//            if(character.isDigit() || character == '.') {
//
//                currDigit += character
//
//            } else {
//                list.add(currDigit.toFloat())
//                currDigit = ""
//                list.add(character)
//            }
//        }
//
//        if(currDigit != "") {
//
//            list.add(currDigit.toFloat())
//
//        }
//
//        return list
//     }


//    private fun calcTimesDiv(passedList : MutableList<Any>): MutableList<Any> {
//
//        val newList = mutableListOf<Any>()
//
//        for(i in passedList.indices) {
//
//            if(passedList[i] is Char && i != passedList.lastIndex) {
//
//                val operator = passedList[i]
//                val prevDigit = passedList[i-1] as Float
//                val nextDigit = passedList[i+1 ] as Float
//
//                when(operator) {
//
//                    'X' -> {
//                        newList.add(prevDigit * nextDigit)
//                        newList.addAll(passedList.subList(i+2, passedList.size))
//                        return newList
//                    }
//                    '/' -> {
//                        newList.add(prevDigit / nextDigit)
//                        newList.addAll(passedList.subList(i+2, passedList.size))
//                        return newList
//                    }
//                    '%' -> {
//                        newList.add(prevDigit % nextDigit)
//                        newList.addAll(passedList.subList(i+2, passedList.size))
//                        return newList
//                    }
//                }
//            } else {
//                newList.add(passedList[i])
//            }
//        }
//        return newList
//    }

    private fun digitOperators(): MutableList<Any> {

        val digits = mutableListOf<Any>()
        val expression = binding.calculatingArea.text.toString()
        var index = 0

        while (index < expression.length) {

            val currentChar = expression[index]

            if (currentChar.isDigit()) {

                // Extract the whole number

                val number = StringBuilder()
                while (index < expression.length && expression[index].isDigit()) {

                    number.append(expression[index])
                    index++

                }

                digits.add(number.toString().toFloat())

            } else if (currentChar == '.') {

                // Extract the decimal number

                val number = StringBuilder()
                while (index < expression.length && (expression[index].isDigit() || expression[index] == '.')) {

                    number.append(expression[index])
                    index++

                }

                digits.add(number.toString().toFloat())

            } else if (currentChar == '-' && index == 0) {

                // Handle negative numbers

                val number = StringBuilder()
                number.append(currentChar)
                index++
                while (index < expression.length && (expression[index].isDigit() || expression[index] == '.')) {

                    number.append(expression[index])
                    index++

                }

                digits.add(number.toString().toFloat())

            } else {

                // Add operator to the list

                digits.add(currentChar)
                index++

            }
        }
        return digits
    }


}
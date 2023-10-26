package bosh.calculator112.activities

import android.text.method.ScrollingMovementMethod
import android.widget.Button
import bosh.calculator112.R
import bosh.calculator112.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : BaseActivity() {
    override val vb by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun initUI() {
        super.initUI()

        handleButtons()

        vb.tvResult.apply {
            movementMethod = ScrollingMovementMethod()
            isSingleLine = true
        }
    }

    private fun handleButtons() {
        val btnNumbers = arrayOf(
                vb.btn0, vb.btn1, vb.btn2, vb.btn3, vb.btn4,
                vb.btn5, vb.btn6, vb.btn7, vb.btn8, vb.btn9
        )

        val btnOperators = arrayOf(vb.btnPlus, vb.btnMinus, vb.btnMultiply, vb.btnDivide)

        fun getLastOperand(): String {
            return vb.tvResult.text.split(*btnOperators.map { it.text.single() }.toCharArray()).last()
        }

        fun getLastOperators(): String {
            return vb.tvResult.text.split(*btnNumbers.map { it.text.single() }.toCharArray()).last()
        }

        fun CharSequence.isDecimal() = !substring(indexOf('.') + 1).all { it == '0' }

        fun isClearRequired(): Boolean {
            vb.tvResult.text.apply {
                if (toString() == getString(R.string.text_invalid_expression)
                        || toString() == getString(R.string.text_fatal_error)
                        || toString() == getString(R.string.text_infinity)) {
                    return true
                }
            }

            return false
        }

        fun isLastCharAnOperator() = btnOperators.any { it.text.single() == vb.tvResult.text.last() }

        for (btn in btnNumbers) {
            btn.setOnClickListener {
                vb.tvResult.apply {
                    if (isClearRequired()) text = ""

                    val isLastOperandAllZero = getLastOperand().all { it == vb.btn0.text.single() }

                    if (btn != vb.btn0) {
                        if (isLastOperandAllZero && getLastOperand().length == 1) {
                            text = text.dropLast(1)
                        }
                    } else {
                        if (text.isNotEmpty() && !isLastCharAnOperator() && isLastOperandAllZero) {
                            return@setOnClickListener
                        }
                    }

                    append(btn.text)
                }
            }
        }

        for (btn in btnOperators) {
            btn.setOnClickListener {
                vb.tvResult.apply {
                    if (isClearRequired()) text = ""

                    if (btn != vb.btnMinus) {
                        if (text.isNullOrEmpty() || text.last() == btn.text.single()) {
                            return@setOnClickListener
                        }

                        if (isLastCharAnOperator()) {
                            text = text.dropLast(
                                    if (text.last() != vb.btnMinus.text.single()) 1
                                    else getLastOperators().length
                            )
                        }
                    } else {
                        if (text.isNotEmpty() && text.last() == btn.text.single()) {
                            return@setOnClickListener
                        }
                    }

                    append(btn.text)
                }
            }
        }

        vb.btnDel.apply {
            setOnClickListener {
                vb.tvResult.apply {
                    text = if (isClearRequired() || text.contains('E')) "" else text.dropLast(1)
                }
            }

            setOnLongClickListener {
                vb.tvResult.apply { if (text.isNotEmpty()) text = "" }

                true
            }
        }

        vb.btnDot.setOnClickListener {
            it as Button

            vb.tvResult.apply {
                if (isClearRequired()) text = ""
                if (!getLastOperand().contains(it.text.single())) append(it.text)
            }
        }

        vb.btnEqual.setOnClickListener {
            vb.tvResult.apply {
                if (text.isNullOrEmpty() || text.none { it in btnNumbers.map { b -> b.text.single() } }) {
                    return@setOnClickListener
                }

                fun CharSequence.trimEndOperator(): CharSequence {
                    return if (!btnOperators.any { it.text.single() == last() }) this
                    else dropLast(1).trimEndOperator()
                }

                val expression = text.trimEndOperator()
                        .replace(getString(R.string.text_cross).toRegex(), "*")
                        .replace(getString(R.string.text_divide).toRegex(), "/")

                text = try {
                    val strAns = ExpressionBuilder(expression).build().evaluate().toFloat().toString()

                    if (strAns.isDecimal() || strAns == getString(R.string.text_infinity)) {
                        strAns
                    } else {
                        strAns.substring(0, strAns.indexOf(vb.btnDot.text.single()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    when (e) {
                        is NumberFormatException, is ArithmeticException -> {
                            getString(R.string.text_invalid_expression)
                        }

                        else -> {
                            getString(R.string.text_fatal_error)
                        }
                    }
                }

                requestLayout()
            }
        }
    }
}

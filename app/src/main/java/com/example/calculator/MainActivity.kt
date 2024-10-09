package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraApp()
        }
    }
}

@Composable
fun CalculadoraApp() {
    var displayText by remember { mutableStateOf("0") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight().padding(16.dp)
        ) {
            Text(
                text = displayText,
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                maxLines = 1
            )

            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "=", "+"),
                listOf("C", "⌫")
            )

            for (row in buttons) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    for (button in row) {
                        CalculatorButton(
                            symbol = button,
                            onClick = {
                                displayText = updateDisplay(displayText, button)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
    ) {
        Text(text = symbol, style = TextStyle(fontSize = 24.sp))
    }
}

fun updateDisplay(currentDisplay: String, input: String): String {
    return if (input == "=") {
        try {
            evalExpression(currentDisplay).toString()
        } catch (e: Exception) {
            "Error"
        }
    } else if (input == "C") {
        "0"
    } else if (input == "⌫") {
        deleteCharacter(currentDisplay)
    } else if (currentDisplay == "0") {
        input
    } else {
        currentDisplay + input
    }
}

fun deleteCharacter(currentDisplay: String): String {
    val result = currentDisplay.dropLast(1)
    if(result == ""){
        return "0"
    }
    return result
}

fun evalExpression(expression: String): Double {
    return try {
        val result = evalOperador(expression)
        result
    } catch (e: Exception) {
        0.0
    }
}

fun evalOperador(expression: String): Double {
    return try {
        val regex = Regex("([0-9]+(?:\\.[0-9]+)?|[-+*/])")
        val tokens = regex.findAll(expression).map { it.value }.toList()

        val intermediateResult = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/") {
                val prevNumber = intermediateResult.removeAt(intermediateResult.size - 1).toDouble()
                val nextNumber = tokens[i + 1].toDouble()
                val result = if (token == "*") prevNumber * nextNumber else prevNumber / nextNumber
                intermediateResult.add(result.toString())
                i += 2
            } else {
                intermediateResult.add(token)
                i++
            }
        }

        var finalResult = intermediateResult[0].toDouble()
        i = 1
        while (i < intermediateResult.size) {
            val operator = intermediateResult[i]
            val nextNumber = intermediateResult[i + 1].toDouble()
            finalResult = if (operator == "+") {
                finalResult + nextNumber
            } else {
                finalResult - nextNumber
            }
            i += 2
        }

        finalResult
    } catch (e: Exception) {
        0.0
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    CalculadoraApp()
}
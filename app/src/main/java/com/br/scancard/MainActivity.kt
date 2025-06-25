package com.br.scancard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.br.scancard.ui.theme.ScanCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Simple(
                        modifier = Modifier.padding(innerPadding),
                        cardNumber = "",
                        cardNumberMaxLength = 16,
                        cardValidity = "",
                        cardCvv = "Android",
                        cardCvvLength = 4,
                        isNumberCardValid = false,
                    )
                }
            }
        }
    }
}

@Composable
fun Simple(
    modifier: Modifier = Modifier,
    cardNumber: String,
    cardNumberMaxLength: Int,
    cardValidity: String,
    cardCvv: String,
    cardCvvLength: Int,
    isNumberCardValid: Boolean,
) {
    Column(modifier = modifier) {
        Text(text = "Card number: $cardNumber")
        Text(text = "Card number max length: $cardNumberMaxLength")
        Text(text = "Card validity: $cardValidity")
        Text(text = "Card CVV: $cardCvv")
        Text(text = "Card CVV length: $cardCvvLength")
        Text(text = "Card number is valid? $isNumberCardValid")
    }
    val context = LocalContext.current
    Button(onClick = {
        ActivityCompat.startActivity(
            context,
            Intent(context, CameraScanActivity::class.java),
            null
        )
    }) {
        Text(text = "Scan card")
    }
}
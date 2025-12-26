package com.br.scancard

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.br.scancard.ui.theme.ScanCardTheme

class MainActivity : ComponentActivity() {

    var cardNumber = ""
    var cardNumberMaxLength = 16
    var cardValidity = ""
    var cardCvv = "Android"
    var cardCvvLength = 4
    var isNumberCardValid = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanCardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Simple(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        cardNumber = cardNumber,
                        cardNumberMaxLength = cardNumberMaxLength,
                        cardValidity = cardValidity,
                        cardCvv = cardCvv,
                        cardCvvLength = cardCvvLength,
                        isNumberCardValid = isNumberCardValid,
                        activity = this
                    )
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if (resultCode == ScanCardActivity.CAMERA_NOT_GRANTED_CODE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                ScanCardActivity.CAMERA_PERMISSION_CODE
            )
        }

        if (requestCode == 100 && data != null) {
                cardNumber = data.getStringExtra(ScanCardActivity.CARD_NUMBER) ?: ""
                cardNumberMaxLength = data.getIntExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, 0)
                cardValidity = data.getStringExtra(ScanCardActivity.CARD_VALIDITY) ?: ""
                cardCvv = data.getStringExtra(ScanCardActivity.CARD_CVV) ?: ""
                cardCvvLength = data.getIntExtra(ScanCardActivity.CARD_CVV_LENGTH, 0)
                isNumberCardValid = data.getBooleanExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, false)
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
    activity: Activity
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Card number: $cardNumber")
        Text(text = "Card number max length: $cardNumberMaxLength")
        Text(text = "Card validity: $cardValidity")
        Text(text = "Card CVV: $cardCvv")
        Text(text = "Card CVV length: $cardCvvLength")
        Text(text = "Card number is valid? $isNumberCardValid")

        val context = LocalContext.current
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("CardScan", "camera permission not granted")
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    ScanCardActivity.CAMERA_PERMISSION_CODE
                )
            } else {
                ActivityCompat.startActivityForResult(
                    activity,
                    Intent(context, ScanCardActivity::class.java),
                    100,
                    null
                )
            }
        }) {
            Text(text = "Scan card")
        }
    }
}
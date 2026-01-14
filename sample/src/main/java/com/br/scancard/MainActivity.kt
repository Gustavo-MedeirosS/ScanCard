package com.br.scancard

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.br.scan_card.ScanCardActivity
import com.br.scancard.ui.theme.ScanCardTheme

class MainActivity : ComponentActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var cardNumber = ""
    var cardNumberMaxLength = 16
    var cardValidity = ""
    var cardCvv = ""
    var cardCvvLength = 4
    var isNumberCardValid = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanCardTheme {
                MainScreen(onClick = { scanCard() })
            }
        }

        activityResultLauncher = registerForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                handleScanCardResult(data = result.data)
            }
        }
    }

    private fun scanCard() {
        val intent = Intent(this, ScanCardActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    private fun handleScanCardResult(data: Intent?) {
        if (data != null) {
            cardNumber = data.getStringExtra(ScanCardActivity.CARD_NUMBER) ?: ""
            cardNumberMaxLength = data.getIntExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, 0)
            cardValidity = data.getStringExtra(ScanCardActivity.CARD_VALIDITY) ?: ""
            cardCvv = data.getStringExtra(ScanCardActivity.CARD_CVV) ?: ""
            cardCvvLength = data.getIntExtra(ScanCardActivity.CARD_CVV_LENGTH, 0)
            isNumberCardValid = data.getBooleanExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, false)
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
    }
}
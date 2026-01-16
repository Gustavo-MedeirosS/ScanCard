package com.br.scancard

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.br.scan_card.CreditCardData
import com.br.scan_card.ScanCardActivity
import com.br.scancard.ui.theme.ScanCardTheme

class MainActivity : ComponentActivity() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(owner = this)[MainViewModel::class.java]

        // Compose
        setContent {
            ScanCardTheme { MainScreen(onClick = { scanCard() }) }
        }

        activityResultLauncher = registerForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val cardData = result.data?.getParcelableExtra(
                    ScanCardActivity.CREDIT_CARD_DATA,
                    CreditCardData::class.java
                )
                viewModel.updateCardInfo(cardData)
            }
        }
    }

    private fun scanCard() {
        val intent = Intent(this, ScanCardActivity::class.java)
        activityResultLauncher.launch(intent)
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
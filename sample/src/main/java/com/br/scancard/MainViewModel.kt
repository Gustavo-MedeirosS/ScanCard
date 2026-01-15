package com.br.scancard

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.br.scan_card.CreditCardHelper
import com.br.scan_card.ScanCardActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.github.gustavomedeiros.scancard.sample.R

class MainViewModel : ViewModel() {

    val cardNumber: MutableStateFlow<String?> = MutableStateFlow(null)
    val cardNumberMaxLength: MutableStateFlow<Int?> = MutableStateFlow(null)
    val cardValidity: MutableStateFlow<String?> = MutableStateFlow(null)
    val cardCvv: MutableStateFlow<String?> = MutableStateFlow(null)
    val cardCvvLength: MutableStateFlow<Int?> = MutableStateFlow(null)
    val isNumberCardValid: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val cardFlagResId: MutableStateFlow<Int?> = MutableStateFlow(null)

    fun updateCardInfo(data: Intent) {
        cardNumber.update { formatCardNumber(data.getStringExtra(ScanCardActivity.CARD_NUMBER)) }
        cardNumberMaxLength.update { data.getIntExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, -1) }
        cardValidity.update { data.getStringExtra(ScanCardActivity.CARD_VALIDITY) }
        cardCvv.update { data.getStringExtra(ScanCardActivity.CARD_CVV) }
        cardCvvLength.update { data.getIntExtra(ScanCardActivity.CARD_CVV_LENGTH, -1) }
        isNumberCardValid.update { data.getBooleanExtra(ScanCardActivity.CARD_NUMBER_MAX_LENGTH, false) }

        setCardFlag(data.getStringExtra(ScanCardActivity.CARD_FLAG) ?: "")
    }

    fun setCardFlag(flag: String) {
        when (flag) {
            CreditCardHelper.MASTERCARD -> cardFlagResId.update { R.drawable.mastercard }
            CreditCardHelper.VISA -> cardFlagResId.update { R.drawable.visa }
            CreditCardHelper.ELO -> cardFlagResId.update { R.drawable.elo }
            else -> cardFlagResId.update { null }
        }
    }

    private fun formatCardNumber(cardNumber: String?): String? {
        if (cardNumber == null) {
            return null
        }

        val cleanedCardNumber = cardNumber.replace(" ", "").trim()

        var formattedCardNumber = ""

        for (i in cleanedCardNumber.indices) {
            formattedCardNumber += cleanedCardNumber[i]
            if ((i + 1) % 4 == 0 && i != cleanedCardNumber.lastIndex) {
                formattedCardNumber += " "
            }
        }

        return formattedCardNumber
    }
}
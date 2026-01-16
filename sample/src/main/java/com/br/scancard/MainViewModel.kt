package com.br.scancard

import androidx.lifecycle.ViewModel
import com.br.scan_card.CreditCardData
import com.br.scan_card.CreditCardHelper
import com.github.gustavomedeiros.scancard.sample.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _cardData = MutableStateFlow<CreditCardData?>(null)
    val cardData: StateFlow<CreditCardData?> = _cardData.asStateFlow()
    private val _cardFlagResId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val cardFlagResId: StateFlow<Int?> = _cardFlagResId.asStateFlow()

    fun updateCardInfo(data: CreditCardData?) {
        _cardData.update { data }
        _cardData.update { it?.copy(number = formatCardNumber(data?.number ?: "")) }

        setCardFlag(data?.flag ?: "")
    }

    fun setCardFlag(flag: String) {
        when (flag) {
            CreditCardHelper.MASTERCARD -> _cardFlagResId.update { R.drawable.mastercard }
            CreditCardHelper.VISA -> _cardFlagResId.update { R.drawable.visa }
            CreditCardHelper.ELO -> _cardFlagResId.update { R.drawable.elo }
            else -> _cardFlagResId.update { null }
        }
    }

    private fun formatCardNumber(cardNumber: String): String {
        if (cardNumber.isBlank()) {
            return ""
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
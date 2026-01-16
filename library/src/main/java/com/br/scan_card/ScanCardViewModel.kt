package com.br.scan_card

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ScanCardViewModel : ViewModel() {

    private val TAG: String = "ScanCardViewModel"

    private val _cardData = MutableStateFlow<CreditCardData?>(
        CreditCardData(
            number = "",
            numberMaxLength = null,
            validity = "",
            cvv = "",
            cvvLength = null,
            flag = null,
            isNumberValid = false
        )
    )
    val cardData: StateFlow<CreditCardData?> = _cardData.asStateFlow()

    private val _isCardComplete = MutableStateFlow(false)
    val isCardComplete: StateFlow<Boolean> = _isCardComplete.asStateFlow()

    private val _isFlashlightEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFlashlightEnabled: StateFlow<Boolean> = _isFlashlightEnabled.asStateFlow()

    fun analyzeCard(text: Text) {
        val lines = text.textBlocks.flatMap { it.lines }

        lines.forEach { line ->
            val lineText = line.text

            // Card Number (13-16 digits)
            if (
                (_cardData.value?.isNumberValid == false || _cardData.value?.isNumberValid == null)
                && _cardData.value?.number.isNullOrBlank()
                && Regex("""\b(?:\d[ -]*?){13,16}\b""").matches(lineText)
            ) {
                Log.i(TAG, "Number card detected: $lineText")

                val cleanedCardNumber = lineText.replace(" ", "").trim()
                _cardData.update { data ->
                    data?.copy(
                        number = lineText,
                        flag = CreditCardHelper.getCardFlag(number = cleanedCardNumber),
                        isNumberValid = CreditCardHelper.isCardNumberValid(number = cleanedCardNumber)
                    )
                }

                Log.i(TAG, "Is card number valid? ${_cardData.value?.isNumberValid}")
            }

            // Validity (12/24, 12-24, 12.24)
            if (
                _cardData.value?.validity.isNullOrBlank()
                && Regex("""\d{2}[/\-.]\d{2}""").matches(lineText)
            ) {
                Log.i(TAG, "Validity card detected: $lineText")
                _cardData.update { data ->
                    data?.copy(validity = lineText)
                }
            }

            // CVV (3 or 4 digits)
            val match = Regex("""(?i)(CVV|CVC)\D{0,5}(\d{3,4})""").find(lineText)
            if (match != null) {
                Log.i(TAG, "CVV card detected: ${match.groupValues[2]}")
                _cardData.update { data ->
                    data?.copy(cvv = match.groupValues[2])
                }
            } else if (Regex("""^\d{3,4}$""").matches(lineText)) {
                Log.i(TAG, "CVV card detected: $lineText")
                _cardData.update { data ->
                    data?.copy(cvv = lineText)
                }
            }
        }

        if (
            !_cardData.value?.number.isNullOrBlank()
            && !_cardData.value?.validity.isNullOrBlank()
            && _cardData.value?.isNumberValid == true
        ) {
            _isCardComplete.update { true }
        }
    }

    fun enableFlashlight() {
        _isFlashlightEnabled.update { isEnabled -> !isEnabled }
    }

}
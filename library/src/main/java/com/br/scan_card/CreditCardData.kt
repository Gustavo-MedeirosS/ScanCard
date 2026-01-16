package com.br.scan_card

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCardData(
    val number: String = "",
    val numberMaxLength: Int? = null,
    val isNumberValid: Boolean = false,
    val validity: String = "",
    val cvv: String = "",
    val cvvLength: Int? = null,
    val flag: String? = null
) : Parcelable

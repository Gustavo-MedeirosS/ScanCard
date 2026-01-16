package com.br.scan_card

object CreditCardHelper {

    // Card Flags
    const val MASTERCARD = "Mastercard"
    const val VISA = "Visa"
    const val ELO = "Elo"
    const val HIPERCARD = "Hipercard"
    const val AMEX = "Amex"
    const val DISCOVER = "Discover"
    const val DINERS = "Diners"
    const val AURA = "Aura"
    const val JCB = "JCB"

    fun getCardFlag(number: String): String {
        if (isNumberMasterCard(number = number)) {
            return MASTERCARD
        } else if (isNumberElo(number = number)) {
            return ELO
        } else if (isNumberVisa(number = number)) {
            return VISA
        } else if (isNumberAmex(number = number)) {
            return AMEX
        } else if (isNumberHiperCard(number = number)) {
            return HIPERCARD
        } else if (isNumberDiscover(number = number)) {
            return DISCOVER
        } else if (isNumberDinners(number = number)) {
            return DINERS
        } else if (isNumberAura(number = number)) {
            return AURA
        } else if (isNumberJcb(number = number)) {
            return JCB
        } else {
            return ""
        }
    }

    private fun isNumberMasterCard(number: String): Boolean {
        return firstNNumbersAreBetween(number, 6, 510000, 559999) || firstNNumbersAreBetween(
            number,
            6,
            222100,
            272099
        )
    }

    private fun isNumberElo(number: String): Boolean {
        return number.startsWith("4011") ||
                number.startsWith("431274") ||
                number.startsWith("438935") ||
                number.startsWith("451416") ||
                number.startsWith("457393") ||
                number.startsWith("4576") ||
                number.startsWith("457631") ||
                number.startsWith("457632") ||
                number.startsWith("504175") ||
                firstNNumbersAreBetween(number, 6, 506699, 506778) ||
                firstNNumbersAreBetween(number, 6, 509000, 509999) ||
                number.startsWith("627780") ||
                number.startsWith("636297") ||
                number.startsWith("636368") ||
                number.startsWith("636369") ||
                firstNNumbersAreBetween(number, 6, 650031, 650033) ||
                firstNNumbersAreBetween(number, 6, 650035, 650051) ||
                firstNNumbersAreBetween(number, 6, 650405, 650439) ||
                firstNNumbersAreBetween(number, 6, 650485, 650538) ||
                firstNNumbersAreBetween(number, 6, 650541, 650598) ||
                firstNNumbersAreBetween(number, 6, 650700, 650718) ||
                firstNNumbersAreBetween(number, 6, 650720, 650727) ||
                firstNNumbersAreBetween(number, 6, 650901, 650920) ||
                firstNNumbersAreBetween(number, 6, 651652, 651679) ||
                firstNNumbersAreBetween(number, 6, 655000, 655019) ||
                firstNNumbersAreBetween(number, 6, 655021, 655058)
    }

    private fun isNumberAmex(number: String): Boolean {
        return firstNNumbersAreBetween(number, 6, 340000, 349999) || firstNNumbersAreBetween(
            number,
            6,
            370000,
            379999
        )
    }

    private fun isNumberHiperCard(number: String): Boolean {
        return number.startsWith("384100") ||
                number.startsWith("384140") ||
                number.startsWith("384160") ||
                number.startsWith("606282") ||
                number.startsWith("637095") ||
                number.startsWith("637568") ||
                number.startsWith("637599") ||
                number.startsWith("637609") ||
                number.startsWith("637612")
    }

    private fun isNumberDiscover(number: String): Boolean {
        return number.startsWith("6011") ||
                number.startsWith("622") ||
                number.startsWith("64") ||
                number.startsWith("65")
    }

    private fun isNumberDinners(number: String): Boolean {
        return number.startsWith("301") ||
                number.startsWith("305") ||
                number.startsWith("36") ||
                number.startsWith("38")
    }

    private fun isNumberAura(number: String): Boolean {
        return number.startsWith("50")
    }

    private fun isNumberJcb(number: String): Boolean {
        return number.startsWith("35")
    }

    private fun isNumberVisa(number: String): Boolean {
        return number.startsWith("4")
    }

    private fun firstNNumbersAreBetween(
        number: String,
        nNumbers: Int,
        start: Int,
        end: Int
    ): Boolean {
        if (number.length < nNumbers) return false
        val firstNumbers = number.take(nNumbers).toIntOrNull() ?: return false
        return firstNumbers in start..end
    }

    // Luhn's Algorithm to validate card number
    fun isCardNumberValid(number: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in number.length - 1 downTo 0) {
            var n = number.substring(i, i + 1).toInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }
}
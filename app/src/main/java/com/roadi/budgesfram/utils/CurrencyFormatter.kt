package com.roadi.budgesfram.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, currencyCode: String): String {
        return try {
            val format = when (currencyCode) {
                "USD" -> NumberFormat.getCurrencyInstance(Locale.US)
                "EUR" -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
                "RUB" -> {
                    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
                    formatter.currency = Currency.getInstance("RUB")
                    formatter
                }
                "GBP" -> NumberFormat.getCurrencyInstance(Locale.UK)
                else -> NumberFormat.getCurrencyInstance(Locale.US)
            }
            format.format(amount)
        } catch (e: Exception) {
            "$currencyCode ${String.format("%.2f", amount)}"
        }
    }
}

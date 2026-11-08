package com.example.data.model

data class CurrencyItem(
    val code: String,
    val symbol: String,
    val name: String,
    val flag: String
)

object Currencies {
    val ALL = listOf(
        CurrencyItem("USD", "$", "US Dollar", "🇺🇸"),
        CurrencyItem("EUR", "€", "Euro", "🇪🇺"),
        CurrencyItem("GBP", "£", "British Pound", "🇬🇧"),
        CurrencyItem("JPY", "¥", "Japanese Yen", "🇯🇵"),
        CurrencyItem("CAD", "CA$", "Canadian Dollar", "🇨🇦"),
        CurrencyItem("AUD", "AU$", "Australian Dollar", "🇦🇺"),
        CurrencyItem("INR", "₹", "Indian Rupee", "🇮🇳"),
        CurrencyItem("ZAR", "R", "South African Rand", "🇿🇦"),
        CurrencyItem("BRL", "R$", "Brazilian Real", "🇧🇷"),
        CurrencyItem("CHF", "Fr", "Swiss Franc", "🇨🇭"),
        CurrencyItem("SGD", "S$", "Singapore Dollar", "🇸🇬"),
        CurrencyItem("NGN", "₦", "Nigerian Naira", "🇳🇬")
    )

    fun getSymbol(code: String): String {
        return ALL.firstOrNull { it.code.equals(code, ignoreCase = true) }?.symbol ?: "$"
    }

    fun format(price: Double, code: String): String {
        val symbol = getSymbol(code)
        return when (code.uppercase()) {
            "JPY" -> "$symbol${price.toInt()}"
            else -> String.format("%s%.2f", symbol, price)
        }
    }
}

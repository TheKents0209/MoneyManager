package com.example.moneymanager.util

import android.util.Log
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

fun formatMonthDoubleDigits(monthNum: String): String {
    if(monthNum.toInt() < 10) {
        return "0${monthNum}"
    } else {
        return monthNum
    }
}

fun formatStringToDate(dateString: String): LocalDate {
    return LocalDate.parse(dateString)
}

fun formatLocalDateToString(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthValue}.${date.year} (${date.dayOfWeek.getDisplayName(
        TextStyle.SHORT, Locale.getDefault())})"
}

fun getValidatedNumber(text: String): String {
    // Start by filtering out unwanted characters like commas and multiple decimals
    val filteredChars = text.filterIndexed { index, c ->
        c in "0123456789" ||                      // Take all digits
                (c == '.' && text.indexOf('.') == index)  // Take only the first decimal
    }
    // Now we need to remove extra digits from the input
    return if(filteredChars.contains('.')) {
        val beforeDecimal = filteredChars.substringBefore('.')
        val afterDecimal = filteredChars.substringAfter('.')
        beforeDecimal.take(6) + "." + afterDecimal.take(2)    // If decimal is present, take first 6 digits before decimal and first 2 digits after decimal
    } else {
        filteredChars.take(6)                     // If there is no decimal, just take the first 6 digits
    }
}

fun currencyStringToInt(input: String?): Int {
    val pattern = Regex("[.,]")
    val newVal = input?.replace(pattern, "")
    if(newVal?.length == 0) {
        return 0
    } else {
        return newVal!!.toInt()
    }
}


fun intToCurrencyString(input: Int?): String {
    if(input?.length()!! >= 3) {
        val str = input.toString()
        val sb = StringBuilder(str)
        val newVal = sb.insert(str.length-2, ".").toString()
        Log.d("INT", newVal)
        return newVal
    }else {
        return "0.00"
    }
}

fun Int.length() = when(this) {
    0 -> 1
    else -> kotlin.math.log10(kotlin.math.abs(toDouble())).toInt() + 1
}
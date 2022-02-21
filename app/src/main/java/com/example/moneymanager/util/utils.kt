package com.example.moneymanager.util

import android.util.Log
import com.example.moneymanager.data.model.Transaction
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

fun formatToDoubleDigits(n: String): String {
    if (n.toInt() < 10) {
        return "0${n}"
    } else {
        return n
    }
}

fun formatStringToDate(dateString: String): LocalDate {
    return LocalDate.parse(dateString)
}

fun formatLocalDateToString(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthValue}.${date.year} (${
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    })"
}

//Sample taken from https://stackoverflow.com/questions/70058829/limit-number-of-digits-before-and-after-decimals-in-text-field-in-jetpack-compos
fun getValidatedNumber(text: String): String {
    // Start by filtering out unwanted characters like commas and multiple decimals
    val filteredChars = text.filterIndexed { index, c ->
        c in "0123456789" ||                      // Take all digits
        (c == '.' && text.indexOf('.') == index)  // Take only the first decimal
    }
    // Now we need to remove extra digits from the input
    return if (filteredChars.contains('.')) {
        val beforeDecimal = filteredChars.substringBefore('.')
        val afterDecimal = filteredChars.substringAfter('.')
        beforeDecimal.take(6) + "." + afterDecimal.take(2)    // If decimal is present, take first 6 digits before decimal and first 2 digits after decimal
    } else {
        filteredChars.take(6)              // If there is no decimal, just take the first 6 digits
    }
}

fun validateAmount(text: String?): String {
    if(text != null && text != "") {
        return String.format("%.2f", text.toFloat())
    }else {
        return "0.00"
    }
}

fun currencyStringToInt(input: String?): Int {
    val pattern = Regex("[.,]")
    var newVal = input?.replace(pattern, "")
    newVal = (newVal?.toInt()?.times(100)).toString()
    return if (newVal.isEmpty()) {
        0
    } else {
        newVal.toInt()
    }
}


fun intToCurrencyString(input: Int?): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    Log.d("input", input.toString())
    if (input != null && input.length() >= 4) {
        val div = input/100
        val str = div.toString()
        val sb = StringBuilder(str)
        val newVal = sb.insert(str.length - 2, ".").toString()
        return formatter.format(newVal.toDouble())
    } else if(input != null && input.length() <= 3){
        val div = input/10
        val str = div.toString()
        val sb = StringBuilder(str)
        val newVal = sb.insert(str.length - 2, ".0").toString()
        return formatter.format(newVal.toDouble())
    } else {
        return formatter.format("0.00".toDouble())
    }
}

fun Int.length() = when (this) {
    0 -> 1
    else -> kotlin.math.log10(kotlin.math.abs(toDouble())).toInt() + 1
}

fun listDifferentDays(list: List<Transaction>?): List<String> {
    val listOfDays = mutableListOf<String>()
    list?.forEach {
        if(!listOfDays.contains(formatStringToDate(it.date).dayOfMonth.toString())) {
            listOfDays += formatStringToDate(it.date).dayOfMonth.toString()
        }
    }
    return listOfDays
}

fun areAllRequiredFieldsFilled(tViewModel: TransactionViewModel): Boolean {
    return tViewModel.category.value != "" &&
            tViewModel.accountId.value != 0L &&
            tViewModel.amount.value != "" &&
            tViewModel.amount.value != "0.00"
}
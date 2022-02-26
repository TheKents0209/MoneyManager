package com.example.moneymanager.util

import androidx.compose.ui.graphics.Color
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.model.Transaction
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.random.Random

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

fun intToCurrencyString(input: Int?): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    if(input != null) {
        if(input.length() >= 2) {
            val div = input
            val str = div.toString()
            val sb = StringBuilder(str)
            val newVal = sb.insert(str.length - 2, ".").toString()
            return formatter.format(newVal.toDouble())
        } else if(input.length() == 1) {
            val div = input
            val str = div.toString()
            val sb = StringBuilder(str)
            val newVal = sb.insert(str.length -1, ".0").toString()
            return formatter.format(newVal.toDouble())
        }
    }
    return formatter.format("0.00".toDouble())
}

fun currencyStringToInt(input: String?): Int {
    val pattern = Regex("[^0-9]")
    val newVal = input?.replace(pattern, "")

    return if(newVal.isNullOrEmpty()) {
        0
    } else {
        newVal.toInt()
    }
}

fun validateAmount(text: String?): String {
    return if(text != null && text != "" && text != "." && text != ",") {
        String.format("%.2f", text.toFloat())
    }else {
        "0.00"
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

fun listDifferentGroups(list: List<Account>?): List<String> {
    val listOfGroups = mutableListOf<String>()
    list?.forEach {
        if(!listOfGroups.contains(it.group)) {
            listOfGroups += it.group
        }
    }
    return listOfGroups
}

fun listDifferentCategorysAndAmounts(list: List<Transaction>?): Map<String, Int> {
    val map = mutableMapOf<String, Int>()

    list?.forEach {
        if (!map.containsKey(it.category)) {
            map.put(it.category, it.amount)
        } else {
            val currentValue = map.getValue(it.category)
            map.put(it.category, currentValue + it.amount)
        }
    }
    //https://www.programiz.com/kotlin-programming/examples/sort-map-values
    return map.toList().sortedBy { (_, value) -> value }.asReversed().toMap()
}

fun areAllRequiredFieldsFilled(tViewModel: TransactionViewModel): Boolean {
    return tViewModel.category.value != "" &&
            tViewModel.accountId.value != 0L &&
            tViewModel.amount.value != 0
}

fun pickColor(index: Int) : Color {
    //TODO: Generate color scale that's visibly different enough
    val colors = mutableListOf(
        Color(0XFFFF4744),
        Color(0XFFFF7F44),
        Color(0XFFFFB744),
        Color(0XFFFFEF44),
        Color(0XFFD7FF44),
        Color(0XFF9FFF44),
        Color(0XFF66FF44),
        Color(0XFF44FF92),
        Color(0XFF44FCFF),
        Color(0XFFFF5722),
        Color(0XFF795548),
        Color(0XFF9E9E9E),
        Color(0XFF607D8B)
    )
    //Failsafe if colors list run out
    if(colors.size >= index) {
        return colors[index]
    }
    return Color(
            Random.nextInt(0, 255),
            Random.nextInt(0, 255),
            Random.nextInt(0, 255)
    )
}
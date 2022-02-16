package com.example.moneymanager.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

fun formatMonthDoubleDigits(monthNum: String): String {
    if(monthNum.toInt() < 10) {
        return "0${monthNum}"
    } else {
        return monthNum
    }
}

fun formatStringToDate(dateString: String): LocalDateTime {
    return LocalDateTime.parse(dateString)
}

fun formatLocalDateToString(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthValue}.${date.year} (${date.dayOfWeek.getDisplayName(
        TextStyle.SHORT, Locale.getDefault())})"
}
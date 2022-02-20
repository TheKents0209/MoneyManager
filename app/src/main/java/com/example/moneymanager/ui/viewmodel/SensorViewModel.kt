package com.example.moneymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SensorViewModel: ViewModel() {
    var isShaken: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun shake() {
        isShaken.value = true
    }
    fun reset() {
        isShaken.value = false
    }
}
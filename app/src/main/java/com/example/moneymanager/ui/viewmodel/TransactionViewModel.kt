package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.model.Transaction
import com.example.moneymanager.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionRepository) : AndroidViewModel(Application()) {

    private val _type = MutableLiveData(-1)
    val type: LiveData<Int> = _type

    fun onTypeChange(newType: Int) {
        _type.value = newType
    }


    val transactions = transactionRepository.getAllTransactions()

    fun transactionsMonthly(params: String) = transactionRepository.getTransactionsByMonth(params)

    fun insertTransaction(t: Transaction) = viewModelScope.launch {
        transactionRepository.insertTransaction(t)
    }
    fun insertTransaction() = viewModelScope.launch {
        transactionRepository.insertTransaction(Transaction(0, type.value!!, "2022-02-16", "cat", 1, 10f, "", ""))
    }
}
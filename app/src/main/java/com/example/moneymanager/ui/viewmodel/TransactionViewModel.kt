package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.moneymanager.data.repository.TransactionRepository

class TransactionViewModel(private val transactionRepository: TransactionRepository) : AndroidViewModel(Application()) {
    val transactions = transactionRepository.getAllTransactions()

    fun transactionsMonthly(params: String) = transactionRepository.getTransactionsByMonth(params)
}
package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.model.Transaction
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val database = DB.getInstance(application)

    fun getAllTransactions(): LiveData<List<Transaction>> = database.TransactionDao().getAll()
    fun getTransactionsByMonth(params: String): LiveData<List<Transaction>> = database.TransactionDao().getTransactionsByMonth(params)

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            database.TransactionDao().insert(transaction)
        }
    }


    fun getAllAccounts(): LiveData<List<Account>> = database.AccountDao().getAll()

    fun insertAccount(account: Account) {
        viewModelScope.launch {
            database.AccountDao().insert(account)
        }
    }
}
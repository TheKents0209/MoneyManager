package com.example.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.example.moneymanager.data.dao.TransactionDao
import com.example.moneymanager.data.model.Transaction

class TransactionRepository(private val dbDao: TransactionDao) {

    fun getAllTransactions(): LiveData<List<Transaction>> = dbDao.getAll()
    fun getTransactionsByMonth(params: String): LiveData<List<Transaction>> = dbDao.getTransactionsByMonth(params)
}
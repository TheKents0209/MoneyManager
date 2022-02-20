package com.example.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.example.moneymanager.data.dao.TransactionDao
import com.example.moneymanager.data.model.Transaction

class TransactionRepository(private val dbDao: TransactionDao) {

    fun getAllTransactions(): LiveData<List<Transaction>> = dbDao.getAll()
    fun getTransactionsByMonth(params: String): LiveData<List<Transaction>> = dbDao.getTransactionsByMonth(params)
    fun getTransactionByTypeAndMonth(type: Int, params: String): LiveData<List<Transaction>> = dbDao.getTransactionsByTypeAndMonth(type, params)
    fun getTransactionsByTypeAndDay(type: Int, params: String): LiveData<Int> = dbDao.getTransactionsSumByTypeAndDay(type, params)

    fun getTransactionsSumByTypeAndMonth(type: Int, params: String): LiveData<Int> = dbDao.getTransactionsSumByTypeAndMonth(type, params)
    fun getTransactionsTotalMonth(params: String): LiveData<Int> = dbDao.getTransactionsTotalMonth(params)

    suspend fun insertTransaction(t: Transaction) = dbDao.insert(t)
}
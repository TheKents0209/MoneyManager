package com.example.moneymanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymanager.data.model.Transaction

@Dao
interface TransactionDao : GenericDao<Transaction> {
    @Query("SELECT * FROM transactions")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE transactions.transactionId = :id")
    fun getTransactionWithId(id: Long): LiveData<Transaction>

    @Query("SELECT * FROM transactions WHERE date LIKE :param")
    fun getTransactionsByMonth(param: String): LiveData<List<Transaction>>
}
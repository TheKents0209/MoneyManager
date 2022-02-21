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

    @Query("SELECT * FROM transactions WHERE transactions.type = :type AND date LIKE :params")
    fun getTransactionsByTypeAndMonth(type: Int, params: String): LiveData<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactions.type = :type AND date LIKE :params")
    fun getTransactionsSumByTypeAndDay(type: Int, params: String): LiveData<Int>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactions.type = :type AND date LIKE :params")
    fun getTransactionsSumByTypeAndMonth(type: Int, params: String): LiveData<Int>

    @Query("SELECT COALESCE( (SELECT SUM(amount) FROM transactions WHERE transactions.type = 1 AND date LIKE :params), 0) - COALESCE( (SELECT SUM(amount) FROM transactions WHERE transactions.type = -1 AND date LIKE :params), 0)")
    fun getTransactionsTotalMonth(params: String): LiveData<Int>
}
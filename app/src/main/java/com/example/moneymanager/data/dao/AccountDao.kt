package com.example.moneymanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymanager.data.model.Account

@Dao
interface AccountDao : GenericDao<Account> {
    @Query("SELECT * FROM accounts")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT DISTINCT name FROM accounts")
    fun getAllNames(): LiveData<List<String>>

    @Query("SELECT * FROM accounts WHERE accounts.id = :id")
    fun getAccountWithId(id: Long): LiveData<Account>

    @Query("SELECT ((SELECT SUM(amount) FROM accounts WHERE id = :id)+COALESCE( (SELECT SUM(amount) FROM transactions WHERE transactions.type = 1 AND accountId = :id), 0) - COALESCE( (SELECT SUM(amount) FROM transactions WHERE transactions.type = -1 AND accountId = :id), 0))")
    fun getAccountAmount(id: Long): LiveData<Int>
}
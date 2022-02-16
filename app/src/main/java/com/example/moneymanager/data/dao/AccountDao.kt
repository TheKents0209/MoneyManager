package com.example.moneymanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymanager.data.model.Account

@Dao
interface AccountDao : GenericDao<Account> {
    @Query("SELECT * FROM accounts")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM accounts WHERE accounts.id = :id")
    fun getAccountWithId(id: Long): LiveData<Account>
}
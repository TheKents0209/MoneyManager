package com.example.moneymanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moneymanager.data.model.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM accounts WHERE accounts.id = :id")
    fun getAccountWithId(id: Long): LiveData<Account>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)
}
package com.example.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.example.moneymanager.data.dao.AccountDao
import com.example.moneymanager.data.model.Account

class AccountRepository(private val dbDao: AccountDao) {

    fun getAllAccounts(): LiveData<List<Account>> = dbDao.getAll()

    suspend fun insertAccount(account: Account) {
        dbDao.insert(account)
    }
}
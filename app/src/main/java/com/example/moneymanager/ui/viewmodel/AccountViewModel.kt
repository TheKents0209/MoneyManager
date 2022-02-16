package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: AccountRepository) : AndroidViewModel(Application()) {

    val accounts = accountRepository.getAllAccounts()

    fun insertAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insertAccount(account)
        }
    }

}
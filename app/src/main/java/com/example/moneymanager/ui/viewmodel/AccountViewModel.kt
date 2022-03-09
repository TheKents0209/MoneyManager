package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: AccountRepository) : AndroidViewModel(Application()) {
    private val _id = MutableLiveData(0L)
    val id: LiveData<Long> = _id

    private val _group = MutableLiveData("")
    val group: LiveData<String> = _group

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> = _amount

    private val _originalAmount = MutableLiveData(0)
    val originalAmount: LiveData<Int> = _originalAmount

    private val _originalId = MutableLiveData(0L)
    val originalId: LiveData<Long> = _originalId

    private val _includeTotals = MutableLiveData(true)
    val includeTotals: LiveData<Boolean> = _includeTotals

    fun onIdChange(newId: Long) {
        _id.value = newId
    }

    fun setOriginalId(newId: Long) {
        _originalId.value = newId
    }

    fun setOriginalAmount(newAmount: Int) {
        _originalAmount.value = newAmount
    }

    fun onGroupChange(newGroup: String) {
        _group.value = newGroup
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onAmountChange(newAmount: Int) {
        _amount.value = newAmount
    }

    fun onIncludeChange(newBool: Boolean) {
        _includeTotals.value = newBool
    }

    val accounts = accountRepository.getAllAccounts()

    fun getAccountWithId(id: Long) = accountRepository.getAccountWithId(id)
    fun getAccountAmount(id: Long) = accountRepository.getAccountAmount(id)

    fun insertAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insertAccount(account)
        }
    }

    fun insertAccount() {
        viewModelScope.launch {
            accountRepository.insertAccount(Account(id.value!!, group.value!!, name.value!!, amount.value!!, includeTotals.value!!))
        }
    }
    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
        }
    }
}
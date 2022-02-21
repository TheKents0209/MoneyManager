package com.example.moneymanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.model.Transaction
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.util.currencyStringToInt
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionViewModel(private val transactionRepository: TransactionRepository) : AndroidViewModel(Application()) {

    private val _id = MutableLiveData(0L)
    val id: LiveData<Long> = _id

    private val _type = MutableLiveData(-1)
    val type: LiveData<Int> = _type

    private val _date = MutableLiveData(LocalDate.now().toString())
    val date: LiveData<String> = _date

    private val _category = MutableLiveData("")
    val category: LiveData<String> = _category

    private val _accountId = MutableLiveData(0L)
    val accountId: LiveData<Long> = _accountId

    private val _amount = MutableLiveData("")
    val amount: LiveData<String> = _amount

    private val _description = MutableLiveData("")
    val description: LiveData<String> = _description

    private val _imagePath = MutableLiveData("")
    val imagePath: LiveData<String> = _imagePath

    fun onIdChange(newId: Long) {
        _id.value = newId
    }

    fun onTypeChange(newType: Int) {
        _type.value = newType
    }

    fun onDateChange(newDate: String) {
        _date.value = newDate
    }

    fun onCategoryChange(newCategory: String) {
        _category.value = newCategory
    }

    fun onAccountIdChange(newId: Long) {
        _accountId.value = newId
    }

    fun onAmountChange(newAmount: String) {
        _amount.value = newAmount
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onImagePathChange(newPath: String) {
        _imagePath.value = newPath
    }


    val transactions = transactionRepository.getAllTransactions()

    fun transactionWithId(id: Long) = transactionRepository.getTransactionWithId(id)
    fun transactionsByTypeDaily(type: Int, params: String) = transactionRepository.getTransactionsByTypeAndDay(type, params)
    fun transactionsMonthly(params: String) = transactionRepository.getTransactionsByMonth(params)
    fun transactionsByTypeMonthly(type: Int, params: String) = transactionRepository.getTransactionByTypeAndMonth(type, params)
    fun transactionsSumByTypeAndMonth(type: Int, params: String) = transactionRepository.getTransactionsSumByTypeAndMonth(type, params)
    fun transactionsTotalMonthly(params: String) = transactionRepository.getTransactionsTotalMonth(params)

    fun insertTransaction() = viewModelScope.launch {
        //null checks
        transactionRepository.insertTransaction(Transaction(id.value!!, type.value!!, date.value!!, category.value!!, accountId.value!!, currencyStringToInt(amount.value), description.value!!, imagePath.value!!))
    }
    fun updateTransaction() = viewModelScope.launch {
        transactionRepository.updateTransaction(Transaction(id.value!!, type.value!!, date.value!!, category.value!!, accountId.value!!, currencyStringToInt(amount.value), description.value!!, imagePath.value!!))
    }
    fun deleteTransaction() = viewModelScope.launch {
        transactionRepository.deleteTransaction(Transaction(id.value!!, type.value!!, date.value!!, category.value!!, accountId.value!!, currencyStringToInt(amount.value), description.value!!, imagePath.value!!))
    }
}
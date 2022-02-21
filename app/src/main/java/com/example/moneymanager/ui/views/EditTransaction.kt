package com.example.moneymanager.ui.views

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.getValidatedNumber
import com.example.moneymanager.util.intToCurrencyString

@Composable
fun EditTransaction(transactionId: Long?, navController: NavController) {
    val tViewModel = TransactionViewModel(TransactionRepository(DB.getInstance(LocalContext.current).TransactionDao()))

    Log.d("tID", transactionId.toString())
    if(transactionId != null) {
        val transaction = tViewModel.transactionWithId(transactionId).observeAsState().value
        if(transaction != null) {
            tViewModel.onIdChange(transaction.transactionId)
            tViewModel.onTypeChange(transaction.type)
            tViewModel.onDateChange(transaction.date)
            tViewModel.onCategoryChange(transaction.category)
            tViewModel.onAccountIdChange(transaction.accountId)
            tViewModel.onAmountChange(getValidatedNumber(intToCurrencyString(transaction.amount)))
            tViewModel.onDescriptionChange(transaction.description)
            tViewModel.onImagePathChange(transaction.imagePath)
            InsertTransaction(tViewModel, navController, true)
        }
    }
}
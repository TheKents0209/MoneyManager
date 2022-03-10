package com.example.moneymanager.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel

@Composable
fun EditTransaction(transactionId: Long?, navController: NavController) {
    val tViewModel = TransactionViewModel(
        TransactionRepository(
            DB.getInstance(LocalContext.current).TransactionDao()
        )
    )
    val aViewModel =
        AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))

    if (transactionId != null) {
        val transaction = tViewModel.transactionWithId(transactionId).observeAsState().value
        if (transaction != null) {
            tViewModel.onIdChange(transaction.transactionId)
            tViewModel.onTypeChange(transaction.type)
            tViewModel.onDateChange(transaction.date)
            tViewModel.onCategoryChange(transaction.category)
            tViewModel.onAccountIdChange(transaction.accountId)
            aViewModel.setOriginalId(transaction.accountId)
            aViewModel.setOriginalAmount(
                aViewModel.getAccountWithId(transaction.accountId).observeAsState().value?.amount
                    ?: 0
            )
            tViewModel.onAmountChange(transaction.amount)
            tViewModel.onDescriptionChange(transaction.description)
            tViewModel.onImagePathChange(transaction.imagePath)
            InsertTransaction(tViewModel, aViewModel, navController, true)
        }
    }
}
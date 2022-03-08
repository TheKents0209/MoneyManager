package com.example.moneymanager.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.components.AmountRow
import com.example.moneymanager.ui.components.GroupAlertDialog
import com.example.moneymanager.ui.components.InsertAccountButton
import com.example.moneymanager.ui.components.NameAlertDialog
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel

@Composable
fun InsertAccount(navController: NavController) {

    val tViewModel = TransactionViewModel(TransactionRepository(DB.getInstance(LocalContext.current).TransactionDao()))
    val aViewModel = AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))

    Column {
        GroupAlertDialog(aViewModel)
        NameAlertDialog(aViewModel)
        AmountRow(aViewModel)
        InsertAccountButton(aViewModel, navController)
    }
}
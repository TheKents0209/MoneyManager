package com.example.moneymanager.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.util.intToCurrencyString
import com.example.moneymanager.util.listDifferentGroups

@Composable
fun AccountsScreen(navController: NavController) {
    val aViewModel = AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))

    val list = aViewModel.accounts.observeAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        //TODO: Account text to top
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {
//                aViewModel.insertAccount(Account(0, "asd", "asdasd", Random.nextInt(1000, 10000), true))
                navController.navigate("addAccount")
            }) {
                Icon(
                    painterResource(R.drawable.ic_twotone_add_24), contentDescription = "Add account"
                )
            }
            IconButton(onClick = {  }) {
                Icon(
                    painterResource(R.drawable.ic_twotone_edit_24), contentDescription = "Modify accounts"
                )
            }
        }
        LazyColumn() {
            val groups = listDifferentGroups(list)
            groups.forEach { groupString ->
                item() {
                    Text(groupString, color = Color.Red)
                    Column() {
                        list?.forEach {
                            if(groupString == it.group) {
                                Text(text = it.name + intToCurrencyString(aViewModel.getAccountAmount(it.id).observeAsState().value), modifier = Modifier.clickable {
                                    aViewModel.deleteAccount(Account(it.id, it.group, it.name, it.amount, it.includeInTotals))
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
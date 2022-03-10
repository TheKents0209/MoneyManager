package com.example.moneymanager.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    var isModifyEnabled by remember { mutableStateOf(false) }

    val list = aViewModel.accounts.observeAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {
                navController.navigate("addAccount")
            }) {
                Icon(
                    painterResource(R.drawable.ic_twotone_add_24), contentDescription = "Add account"
                )
            }
            IconButton(onClick = {
                isModifyEnabled = !isModifyEnabled
            }) {
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
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = it.name + intToCurrencyString(aViewModel.getAccountAmount(it.id).observeAsState().value), modifier = Modifier.align(
                                        Alignment.CenterVertically))
                                    if(isModifyEnabled) {
                                        IconButton(onClick = {
                                            aViewModel.deleteAccount(Account(it.id, it.group, it.name, it.amount, it.includeInTotals))
                                        }) {
                                            Icon(
                                                painterResource(R.drawable.ic_twotone_remove_circle_outline_24), contentDescription = "Delete account"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
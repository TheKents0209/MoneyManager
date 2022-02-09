package com.example.moneymanager.ui.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.moneymanager.R
import com.example.moneymanager.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@Composable
fun TransactionScreen(mainViewModel: MainViewModel) {
    var now by rememberSaveable { mutableStateOf(LocalDateTime.now())}
    var income by remember { mutableStateOf(0f) }
    var expense by remember { mutableStateOf(0f) }
    var total by remember { mutableStateOf(income-expense) }

    //TODO: if month value under 10 then add 0 before it ....DOESNT WORK PROPERLY
    val params = "${now.year}%${now.month.value}%"
    Log.d("params", params)
    //val params = "2022_02%"

    var list = mainViewModel.getTransactionsByMonth(params).observeAsState()

    //mainViewModel.insertTransaction(Transaction(0, 1,LocalDateTime.now().toString(), 1, 10f, "", ""))

    val formatter = NumberFormat.getCurrencyInstance()
    //TODO: Chevrons to edges, text positioning inline with chevrons
    Column() {
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                now = now.minusMonths(1)
                Log.d("MONTH", now.toString())
            }) {
                Icon(painterResource(R.drawable.ic_twotone_chevron_left_24), contentDescription = "Previous month")
            }

            Text(text = "${now.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${now.year}")

            IconButton(onClick = {
                now = now.plusMonths(1)
                Log.d("MONTH", now.toString())
            }) {
                Icon(painterResource(R.drawable.ic_twotone_chevron_right_24), contentDescription = "Next month")
            }
        }
        Row(
            Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.income), fontSize = 10.sp)
                Text(text = formatter.format(income), fontSize = 12.sp, color = MaterialTheme.colors.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.expenses), fontSize = 10.sp)
                Text(text = formatter.format(expense), fontSize = 12.sp, color = MaterialTheme.colors.secondary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.total), fontSize = 10.sp)
                Text(text = formatter.format(total), fontSize = 12.sp)
            }
        }
        LazyColumn() {
            list.value?.forEach {
                item {
                    Text(text = "${it.transactionId} = id")
                }
            }

        }
    }
}


package com.example.moneymanager.ui.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.formatMonthDoubleDigits
import com.example.moneymanager.util.formatStringToDate
import com.example.moneymanager.util.intToCurrencyString
import com.example.moneymanager.util.listDifferentDays
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun TransactionScreen() {
    val tViewModel = TransactionViewModel(TransactionRepository(DB.getInstance(LocalContext.current).TransactionDao()))

    var now by rememberSaveable { mutableStateOf(LocalDate.now())}
    val params = "${now.year}_${formatMonthDoubleDigits(now.monthValue.toString())}%"

    val income = tViewModel.transactionsSumByTypeAndMonth(1, params).observeAsState()
    val expense = tViewModel.transactionsSumByTypeAndMonth(-1, params).observeAsState()
    val total = tViewModel.transactionsTotalMonthly(params).observeAsState()


    Log.d("params", params)
    //val params = "2022_02%"

    val list = tViewModel.transactionsMonthly(params).observeAsState()

    //mainViewModel.insertTransaction(Transaction(0, 1,LocalDateTime.now().toString(), 1, 10f, "", ""))

    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    //TODO: Chevrons to edges, text positioning inline with chevrons
    Column(Modifier.fillMaxSize()) {
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
        Divider(thickness = 1.dp)
        Row(
            Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.income), fontSize = 10.sp)
                Text(text = intToCurrencyString(income.value), fontSize = 12.sp, color = MaterialTheme.colors.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.expenses), fontSize = 10.sp)
                Text(text = intToCurrencyString(expense.value), fontSize = 12.sp, color = MaterialTheme.colors.secondary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.total), fontSize = 10.sp)
                Text(text = intToCurrencyString(total.value), fontSize = 12.sp)
            }
        }
        //TODO:Styling
        LazyColumn() {
            //Reversed list for getting most recent transactions first
            val listOfIndividualDays = listDifferentDays(list.value).asReversed()
            //More effective way of doing this?
            //Every different day gets highlighted row and list of transactions for that day
            listOfIndividualDays.forEach { dateString ->
                item {
                    //Highlighted row
                    Divider(thickness = 1.dp)
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)) {
                        Text(text = dateString, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                        Text(text = LocalDate.parse("${now.year}-${formatMonthDoubleDigits(now.monthValue.toString())}-$dateString").dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).toString(), modifier = Modifier.padding(top = 4.dp, start = 2.dp))
                    }
                    Divider(thickness = 1.dp)
                    //List for each transaction that day
                    Column() {
                        list.value?.forEach {
                            if(formatStringToDate(it.date).dayOfMonth.toString() == dateString) {
                                Text(text = it.transactionId.toString())
                            }
                        }
                    }
                }
            }
            list.value?.forEach {
                item {
                    Text(text = "${it.transactionId} = id")
                }
            }
        }
    }
}




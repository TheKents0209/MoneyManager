package com.example.moneymanager.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.moneymanager.R
import com.example.moneymanager.ui.NavigationItem
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

    val params = "${now.year}_${formatMonthDoubleDigits(now.monthValue.toString())}%"
    Log.d("params", params)
    //val params = "2022_02%"

    var list = mainViewModel.getTransactionsByMonth(params).observeAsState()

    //mainViewModel.insertTransaction(Transaction(0, 1,LocalDateTime.now().toString(), 1, 10f, "", ""))

    val formatter = NumberFormat.getCurrencyInstance()
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
        //TODO:Styling
        LazyColumn() {
            val listOfDays = mutableListOf<String>()
            list.value?.forEach {
                if(!listOfDays.contains(formatStringToDate(it.date).dayOfMonth.toString())) {
                    Log.d("listofdays before", listOfDays.toString())
                    Log.d("listofdays what is added", formatStringToDate(it.date).dayOfMonth.toString())
                    listOfDays += formatStringToDate(it.date).dayOfMonth.toString()
                    Log.d("listofdays after", listOfDays.toString())
                }
            }
            //More effective way of doing this?
            listOfDays.forEach { dateString ->
                item {
                    Row() {
                        Text(text = dateString)
                    }
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

fun formatMonthDoubleDigits(monthNum: String): String {
    if(monthNum.toInt() < 10) {
        return "0${monthNum}"
    } else {
        return monthNum
    }
}

fun formatStringToDate(dateString: String): LocalDateTime {
    return LocalDateTime.parse(dateString)
}

@Composable
fun AddTransaction() {
    Column(Modifier.fillMaxSize()) {
        TransactionTypeGroup()
    }
}

@Composable
fun TransactionTypeGroup() {
    val options = listOf(
        stringResource(id = R.string.income),
        stringResource(id = R.string.expenses),
    )
    val expenseString = stringResource(id = R.string.expenses)
    var selectedOption by remember {
        mutableStateOf(expenseString)
    }
    val onSelectionChange = { text: String ->
        selectedOption = text
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        options.forEach { text ->
            Row(
                modifier = Modifier
                    .padding(
                        all = 8.dp,
                    ),
            ) {
                Text(
                    text = text,
                    style = typography.body1.merge(),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 12.dp,
                            ),
                        )
                        .clickable {
                            onSelectionChange(text)
                        }
                        .background(
                            if (text == selectedOption) {
                                MaterialTheme.colors.primaryVariant
                            } else {
                                MaterialTheme.colors.secondaryVariant
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }
    }
}

//TODO: AlertDialog for this
@Composable
fun TransactionInfoFiller() {
    Column() {
        Row() {
            Text(text = "Date")
        }
        Row() {
            Text(text = "Account")
        }
        Row() {
            Text(text = "Category")
        }
        Row() {
            Text(text = "Amount")
        }
        Row() {
            Text(text = "Description")
        }
    }
    Column() {
        Text(text = "Desc and camera")
    }
}




package com.example.moneymanager.ui.views

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.dialog.AccountAlertDialog
import com.example.moneymanager.ui.dialog.CategoryAlertDialog
import com.example.moneymanager.ui.dialog.DateAlertDialog
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.formatMonthDoubleDigits
import com.example.moneymanager.util.formatStringToDate
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.random.Random

@Composable
fun TransactionScreen() {
    var now by rememberSaveable { mutableStateOf(LocalDate.now())}
    val income by remember { mutableStateOf(0f) }
    val expense by remember { mutableStateOf(0f) }
    val total by remember { mutableStateOf(income-expense) }

    val params = "${now.year}_${formatMonthDoubleDigits(now.monthValue.toString())}%"
    Log.d("params", params)
    //val params = "2022_02%"

    val transactionViewModel = TransactionViewModel(TransactionRepository(DB.getInstance(LocalContext.current).TransactionDao()))

    val list = transactionViewModel.transactionsMonthly(params).observeAsState()

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

@Composable
fun AddTransaction() {
    val tViewModel = TransactionViewModel(TransactionRepository(DB.getInstance(LocalContext.current).TransactionDao()))
    Column(Modifier.fillMaxSize()) {
        TransactionTypeSelector(tViewModel)
        TransactionInfoFiller()
        Button(onClick = {
            tViewModel.insertTransaction()
        }) {

        }
    }
}

@Composable
fun TransactionTypeSelector(tViewModel:TransactionViewModel) {
    val options = listOf(
        stringResource(id = R.string.income),
        stringResource(id = R.string.expenses))

    val expenseString = stringResource(id = R.string.expenses)
    var selectedOption by remember { mutableStateOf(expenseString) }

    val selectedOptionInt by tViewModel.type.observeAsState(-1)

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
                            tViewModel.onTypeChange(Random.nextInt(0,100))
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

//TODO: AlertDialog for this, automatically open each section
@Composable
fun TransactionInfoFiller() {
    Column() {
        DateAlertDialog()
        AccountAlertDialog()
        CategoryAlertDialog()
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




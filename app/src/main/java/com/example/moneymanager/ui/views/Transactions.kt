package com.example.moneymanager.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.formatStringToDate
import com.example.moneymanager.util.formatToDoubleDigits
import com.example.moneymanager.util.intToCurrencyString
import com.example.moneymanager.util.listDifferentDates
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun TransactionScreen(navController: NavController) {
    val tViewModel = TransactionViewModel(
        TransactionRepository(
            DB.getInstance(LocalContext.current).TransactionDao()
        )
    )
    val aViewModel =
        AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))

    var now by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val params = "${now.year}_${formatToDoubleDigits(now.monthValue.toString())}%"

    val income = tViewModel.transactionsSumByTypeAndMonth(1, params).observeAsState()
    val expense = tViewModel.transactionsSumByTypeAndMonth(-1, params).observeAsState()
    val total = tViewModel.transactionsTotalMonthly(params).observeAsState()

    val list = tViewModel.transactionsMonthly(params).observeAsState()

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                now = now.minusMonths(1)
            }) {
                Icon(
                    painterResource(R.drawable.ic_twotone_chevron_left_24),
                    contentDescription = "Previous month",
                    modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            Text(
                text = "${
                    now.month.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                } ${now.year}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            IconButton(onClick = {
                now = now.plusMonths(1)
            }) {
                Icon(
                    painterResource(R.drawable.ic_twotone_chevron_right_24),
                    contentDescription = "Next month",
                    modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Divider(thickness = 2.dp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.income), fontSize = 12.sp)
                Text(
                    text = intToCurrencyString(income.value),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.primary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.expenses), fontSize = 12.sp)
                Text(
                    text = intToCurrencyString(expense.value),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.secondary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.total), fontSize = 12.sp)
                Text(text = intToCurrencyString(total.value), fontSize = 14.sp)
            }
        }
        LazyColumn(modifier = Modifier.fillMaxHeight(0.9f)) {
            //Reversed list for getting most recent transactions first
            val listOfIndividualDates = listDifferentDates(list.value).asReversed()
            //More effective way of doing this?
            //Every different day gets highlighted row and list of transactions for that day
            listOfIndividualDates.forEach { dateString ->
                item {
                    //Highlighted row
                    Divider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                //start + text(4.dp)
                                .padding(start = 6.dp)
                                .fillMaxWidth(0.5f),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = LocalDate.parse(dateString).dayOfMonth.toString(),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(
                                text = LocalDate.parse(dateString).dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                ).toString(), modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 6.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = intToCurrencyString(
                                    tViewModel.transactionsByTypeDaily(
                                        1,
                                        dateString
                                    ).observeAsState().value
                                ),
                                modifier = Modifier.padding(4.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colors.primary
                            )
                            Text(
                                text = intToCurrencyString(
                                    tViewModel.transactionsByTypeDaily(
                                        -1,
                                        dateString
                                    ).observeAsState().value
                                ),
                                modifier = Modifier.padding(4.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colors.secondary
                            )
                        }
                    }
                    Divider(thickness = 1.dp)
                    //List for each transaction that day
                    Column {
                        list.value?.asReversed()?.forEach {
                            if (formatStringToDate(it.date).toString() == dateString) {
                                Row(modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        //Opens transaction information
                                        navController.navigate("editTransaction/${it.transactionId}")
                                    }
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(0.7f)) {
                                        Text(
                                            text = it.category,
                                            modifier = Modifier
                                                .fillMaxWidth(.25f)
                                                .align(Alignment.CenterVertically),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colors.secondaryVariant,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = aViewModel.getAccountWithId(it.accountId)
                                                .observeAsState().value?.name ?: "",
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .padding(start = 16.dp),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colors.secondaryVariant,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = intToCurrencyString(it.amount), color =
                                            (if (it.type == 1) {
                                                MaterialTheme.colors.primary
                                            } else {
                                                MaterialTheme.colors.secondary
                                            }),
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically),
                                            fontSize = 16.sp
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




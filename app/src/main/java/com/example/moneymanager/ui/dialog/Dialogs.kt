package com.example.moneymanager.ui.dialog

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.util.formatLocalDateToString
import com.google.accompanist.flowlayout.FlowRow
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate

//TODO: Styling
@Composable
fun DateAlertDialog() {
    Row {
        val openDialog = remember { mutableStateOf(false) }
        val calendarState = rememberSelectableCalendarState(initialSelection = listOf(LocalDate.now()), initialSelectionMode = SelectionMode.Single)
        var dateString by remember { mutableStateOf(formatLocalDateToString(LocalDate.now())) }

        Text(text = "Date")

        TextField(
            value = dateString,
            onValueChange = { dateString = it},
            enabled = false,
            modifier = Modifier
                .clickable { openDialog.value = true},
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor =  MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Select date")
                },
                text = {
                    SelectableCalendar(calendarState = calendarState,
                        monthHeader = { monthState ->  myMonthHeader(monthState = monthState) },
                    )
                    Log.d("DATE", calendarState.selectionState.selection.toString())
                    var selectedDate: LocalDate = LocalDate.now()
                    if(calendarState.selectionState.selection.isNotEmpty()) {
                        selectedDate = calendarState.selectionState.selection[0]
                    }
                    dateString = formatLocalDateToString(selectedDate)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("Confirm")
                    }
                }
            )
        }
    }
}

//TODO: Make chevrons stay put
@Composable
fun myMonthHeader(monthState: MonthState) {
    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
        IconButton(onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }) {
            Icon(painterResource(R.drawable.ic_twotone_chevron_left_24), contentDescription = "Previous month")
        }
        Text("${monthState.currentMonth.month.name} ${monthState.currentMonth.year}")
        IconButton(onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }) {
            Icon(painterResource(R.drawable.ic_twotone_chevron_right_24), contentDescription = "Next month")
        }
    }
}

@Composable
fun AccountAlertDialog() {
    Row {
        val openDialog = remember { mutableStateOf(false) }
        var accountString by remember { mutableStateOf("") }
        val accountViewModel = AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))
        val accounts = accountViewModel.accounts.observeAsState()

        Text(text = "Account")

        TextField(
            value = accountString,
            onValueChange = { accountString = it},
            enabled = false,
            modifier = Modifier
                .clickable { openDialog.value = true},
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor =  MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Select account")
                },
                text = {
                    FlowRow(modifier= Modifier.fillMaxWidth()) {
                        accounts.value?.forEach { account ->
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .background(Color.Blue)
                                    .border(2.dp, Color.DarkGray),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(account.toString())
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("Confirm")
                    }
                }
            )
        }
    }
}
package com.example.moneymanager.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.*
import com.google.accompanist.flowlayout.FlowRow
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate

@Composable
fun TransactionTypeSelector(tViewModel:TransactionViewModel) {
    val options = listOf(
        stringResource(id = R.string.income),
        stringResource(id = R.string.expenses)
    )
    val expenseString = stringResource(id = R.string.expenses)
    val incomeString = stringResource(id = R.string.income)
    var selectedOption by remember { mutableStateOf(expenseString) }
    val currentValue = tViewModel.type.observeAsState().value
    if(currentValue == 1) {
        selectedOption = incomeString
    }

    //Whichever text/option is clicked, changes selectedOption to that text
    val onSelectionChange = { text: String -> selectedOption = text }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        options.forEach { optionText ->
            Row(
                modifier = Modifier
                    .padding(
                        all = 8.dp,
                    ),
            ) {
                Text(
                    text = optionText,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .clip(
                            shape = RoundedCornerShape(
                                size = 4.dp,
                            ),
                        )
                        .clickable {
                            onSelectionChange(optionText)
                            if (optionText == expenseString) {
                                tViewModel.onTypeChange(-1)
                            } else {
                                tViewModel.onTypeChange(1)
                            }
                        }
                        .background(
                            //If optionText matches selectedOption & optionsText is income
                            if (optionText == selectedOption && optionText != expenseString) {
                                //Blue
                                MaterialTheme.colors.primary
                            } else if (optionText == selectedOption && optionText == expenseString) {
                                //Red
                                MaterialTheme.colors.secondary
                            } else {
                                //Gray
                                MaterialTheme.colors.secondaryVariant
                            }
                        )
                        .padding(
                            vertical = 7.dp,
                            horizontal = 5.dp,
                        ),
                )
            }
        }
    }
}
@Composable
fun ModelDialog(text: String, content: @Composable () -> Unit) {
    Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.fillMaxWidth(0.22f)) {
            Text(text = text,
                Modifier.padding(start = 4.dp, end = 8.dp),
                softWrap = false
            )
        }
        content()
    }

}

@Composable
fun DateAlertDialog(tViewModel:TransactionViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Log.d("asdasd", LocalDate.parse(tViewModel.date.observeAsState().value).toString())
    val calendarState = rememberSelectableCalendarState(initialSelection = listOf(LocalDate.parse(tViewModel.date.observeAsState().value)), initialSelectionMode = SelectionMode.Single)
    var dateString by remember { mutableStateOf(formatLocalDateToString(LocalDate.now())) }

    dateString = formatLocalDateToString(LocalDate.parse(tViewModel.date.observeAsState().value))
    ModelDialog(text = "Date") {
        TextField(
            value = dateString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = { dateString = it},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { openDialog.value = true },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor =  MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )
    }

    if (openDialog.value) {
        var selectedDate by remember { mutableStateOf(LocalDate.parse(tViewModel.date.value))}
        Log.d("selectedDate", selectedDate.toString())
       // var selectedDate: LocalDate = LocalDate.parse(tViewModel.date.observeAsState().value)
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                openDialog.value = false
                tViewModel.onDateChange(selectedDate.toString())
            },
            title = {
                Text(text = "Select date")
            },
            text = {
                SelectableCalendar(calendarState = calendarState,
                    monthHeader = { monthState ->  MyMonthHeader(monthState = monthState) },
                )
                Log.d("DATE", calendarState.selectionState.selection.toString())
                if(calendarState.selectionState.selection.isNotEmpty()) {
                    selectedDate = calendarState.selectionState.selection[0]
                }
                dateString = formatLocalDateToString(selectedDate)
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        Log.d("DBG bf", selectedDate.toString())
                        tViewModel.onDateChange(selectedDate.toString())
                    }) {
                    Text("Confirm")
                }
            }
        )
    }
}

//TODO: Make chevrons stay put
@Composable
fun MyMonthHeader(monthState: MonthState) {
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
fun AccountAlertDialog(tViewModel: TransactionViewModel, aViewModel: AccountViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    var accountString by remember { mutableStateOf("") }
    val accounts = aViewModel.accounts.observeAsState()

    accountString = tViewModel.accountId.observeAsState().value?.let {
        aViewModel.getAccountWithId(
            it
        ).observeAsState().value?.name
    }

    ModelDialog(text = "Account") {
        TextField(
            value = accountString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = { accountString = it},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { openDialog.value = true },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor =  MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )
    }
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
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    accounts.value?.forEach { account ->
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .background(Color.Blue)
                                .border(2.dp, Color.DarkGray)
                                .clickable {
                                    accountString = "${account.name} (${account.group})"
                                    tViewModel.onAccountIdChange(account.id)
                                    openDialog.value = false
                                },
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

@Composable
fun CategoryAlertDialog(tViewModel: TransactionViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    var categoryString by remember { mutableStateOf("") }
    //Categorys are static atm, in the future user can add their own
    val categorys = listOf("Food", "Social Life", "Self-development", "Transportation", "Culture", "Household")

    categoryString = tViewModel.category.observeAsState().value ?: ""

    ModelDialog(text = "Category") {
        TextField(
            value = categoryString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = { categoryString = it},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { openDialog.value = true },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                disabledLabelColor =  MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )
    }
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
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    categorys.forEach { category ->
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .background(Color.Blue)
                                .border(2.dp, Color.DarkGray)
                                .clickable {
                                    categoryString = category
                                    tViewModel.onCategoryChange(categoryString)
                                    openDialog.value = false
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(category)
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

@Composable
fun AmountRow(tViewModel: TransactionViewModel) {
    val amountValue = tViewModel.amount.observeAsState().value
    var amountValueString by remember { mutableStateOf(intToCurrencyString(amountValue)) }
    val onAmountChange : ((String) -> Unit) = {
        amountValueString = getValidatedNumber(it)
        tViewModel.onAmountChange(currencyStringToInt(validateAmount(amountValueString)))
    }

    ModelDialog(text = "Amount") {
        TextField(
            value = amountValueString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = onAmountChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Composable
fun DescriptionRow(tViewModel: TransactionViewModel) {
    val descriptionValue by tViewModel.description.observeAsState()
    val onDescriptionChange : ((String) -> Unit) = { tViewModel.onDescriptionChange(it) }

    ModelDialog(text = "Note") {
        TextField(
            value = descriptionValue.toString(),
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = onDescriptionChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Composable
fun InsertTransactionButton(tViewModel: TransactionViewModel, navController: NavController, isUpdate: Boolean) {
    val context = LocalContext.current
    Column() {
        Row(
            Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                when {
                    areAllRequiredFieldsFilled(tViewModel) -> {
                        Log.d("ImagePath", tViewModel.imagePath.value.toString())
                        Log.d("ImagePath", NavigationItem.Transactions.route)
                        if(isUpdate) {
                            tViewModel.updateTransaction()
                        } else {
                            tViewModel.insertTransaction()
                        }
                        navController.navigate(NavigationItem.Transactions.route)
                    }
                    tViewModel.amount.value == 0 -> {
                        Toast.makeText(context, "Required fields aren't filled or value can't be 0", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Required fields aren't filled", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = "Save")
            }
        }
        if(isUpdate) {
            Row(Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
            )
            {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    //AlertDialog opens which contains warning + delete button
                    tViewModel.deleteTransaction()
                    navController.navigate(NavigationItem.Transactions.route)
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                )) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
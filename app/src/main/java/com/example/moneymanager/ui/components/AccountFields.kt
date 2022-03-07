package com.example.moneymanager.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.util.*
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun GroupAlertDialog(aViewModel: AccountViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    var groupString by remember { mutableStateOf("") }
    //Groups are static atm, in the future user can add their own
    val groups = listOf("Cash", "Bank", "Card", "Savings", "Investments")

    groupString = aViewModel.group.observeAsState().value ?: ""

    ModelDialog(text = "Group") {
        TextField(
            value = groupString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = { groupString = it},
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
                    groups.forEach { group ->
                        Box(
                            modifier = Modifier
                                .size(128.dp)
                                .background(Color.Blue)
                                .border(2.dp, Color.DarkGray)
                                .clickable {
                                    groupString = group
                                    aViewModel.onGroupChange(groupString)
                                    openDialog.value = false
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(group)
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
fun NameAlertDialog(aViewModel: AccountViewModel) {
    val nameValue by aViewModel.name.observeAsState()
    val onNameChange : ((String) -> Unit) = { aViewModel.onNameChange(it) }

    ModelDialog(text = "Name") {
        TextField(
            value = nameValue.toString(),
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = onNameChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Composable
fun AmountRow(aViewModel: AccountViewModel) {
    val amountValue = aViewModel.amount.observeAsState().value
    var amountValueString by remember { mutableStateOf(intToCurrencyString(amountValue)) }
    val onAmountChange : ((String) -> Unit) = {
        amountValueString = getValidatedNumber(it)
        aViewModel.onAmountChange(currencyStringToInt(validateAmount(amountValueString)))
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
fun InsertAccountButton(aViewModel: AccountViewModel, navController: NavController) {
    val context = LocalContext.current
    Column() {
        Row(
            Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                when {
                    areAllRequiredAccountFieldsFilled(aViewModel) -> {
                        aViewModel.insertAccount()
                        navController.navigate(NavigationItem.Accounts.route)
                    }
                    else -> {
                        Toast.makeText(context, "Required fields aren't filled", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = "Save")
            }
        }
    }
}
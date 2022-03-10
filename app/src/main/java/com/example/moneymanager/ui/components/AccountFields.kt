package com.example.moneymanager.ui.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupAlertDialog(aViewModel: AccountViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    var groupString by remember { mutableStateOf("") }
    val groups = listOf(stringResource(R.string.cash), stringResource(R.string.bank), stringResource(R.string.card), stringResource(R.string.savings), stringResource(R.string.investments))

    groupString = aViewModel.group.observeAsState().value ?: ""

    ModelDialog(stringResource(R.string.group)) {
        TextField(
            value = groupString,
            textStyle = TextStyle(fontSize = 14.sp),
            onValueChange = { groupString = it },
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
                // Dismiss the dialog when the user clicks outside the dialog or on the back button.
                openDialog.value = false
            },
            title = {
                Text(stringResource(R.string.select_account))
            },
            text = {
                LazyVerticalGrid(GridCells.Fixed(2), modifier = Modifier.fillMaxWidth()) {
                    groups.forEach { group ->
                        item {
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }
}

@Composable
fun NameAlertDialog(aViewModel: AccountViewModel) {
    val nameValue by aViewModel.name.observeAsState()
    val onNameChange : ((String) -> Unit) = { aViewModel.onNameChange(it) }

    ModelDialog(stringResource(R.string.name)) {
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

    ModelDialog(stringResource(R.string.amount)) {
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
    Column {
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
                        Toast.makeText(context, R.string.required_fields_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
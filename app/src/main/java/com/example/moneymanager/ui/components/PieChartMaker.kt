package com.example.moneymanager.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.IndicatorHeight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.util.formatToDoubleDigits
import com.example.moneymanager.util.intToCurrencyString
import com.example.moneymanager.util.listDifferentCategoriesAndAmounts
import com.example.moneymanager.util.pickColor
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import java.time.LocalDate

@Composable
fun PieChart(tViewModel: TransactionViewModel, localDate: LocalDate) {
    val params = "${localDate.year}_${formatToDoubleDigits(localDate.monthValue.toString())}%"

    val listOfTransactions =
        tViewModel.transactionsByTypeMonthly(tViewModel.type.observeAsState().value!!, params)
            .observeAsState()
    val totalAmount =
        tViewModel.transactionsSumByTypeAndMonth(tViewModel.type.observeAsState().value!!, params)
            .observeAsState()
    val listOfCategorys = listDifferentCategoriesAndAmounts(listOfTransactions.value)

    val listOfSlices = mutableListOf<PieChartData.Slice>()
    val sliceColors = mutableListOf<Color>()

    listOfCategorys.forEach {
        listOfSlices.add(
            PieChartData.Slice(it.value.toFloat(), pickColor(sliceColors.size))
        )
        sliceColors.add(pickColor(sliceColors.size))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Tabs(tViewModel, localDate)
        Column(
            modifier = Modifier
                .fillMaxHeight(0.35f)
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (listOfSlices.isNotEmpty()) {
                PieChart(
                    pieChartData = PieChartData(listOfSlices),
                    sliceDrawer = SimpleSliceDrawer(sliceThickness = 100f)
                )
            } else {
                Text(stringResource(R.string.no_data))
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            var i = 0
            listOfCategorys.forEach { (category, amount) ->
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth(0.65f)
                                .padding(4.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = (String.format(
                                    "%.2f",
                                    (amount.toDouble() / totalAmount.value!!) * 100
                                )) + "%",
                                modifier = Modifier.background(sliceColors[i]),
                                color = Color.Black
                            )
                            Text(
                                text = category, modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 6.dp)
                            )
                            i++
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .align(Alignment.CenterVertically),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(text = intToCurrencyString(amount))
                        }
                    }

                    Divider(thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun Tabs(tViewModel: TransactionViewModel, localDate: LocalDate) {
    val params = "${localDate.year}_${formatToDoubleDigits(localDate.monthValue.toString())}%"
    var tabIndex by remember { mutableStateOf(1) }

    val monthlyIncomes = tViewModel.transactionsSumByTypeAndMonth(1, params).observeAsState()
    val monthlyExpenses = tViewModel.transactionsSumByTypeAndMonth(-1, params).observeAsState()
    val tabTitles = mutableListOf(
        "${stringResource(R.string.income)} ${intToCurrencyString(monthlyIncomes.value)}",
        "${stringResource(R.string.expenses)} ${intToCurrencyString(monthlyExpenses.value)}"
    )
    Column() {
        TabRow(selectedTabIndex = tabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, modifier =
                Modifier.background(MaterialTheme.colors.background),
                    onClick = { tabIndex = index },
                    text = { Text(text = title)},
                    unselectedContentColor = MaterialTheme.colors.primaryVariant,
                    selectedContentColor = MaterialTheme.colors.onBackground)
            }
        }
        when (tabIndex) {
            0 -> tViewModel.onTypeChange(1)
            1 -> tViewModel.onTypeChange(-1)
        }
    }
}
package com.example.moneymanager.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.components.PieChart
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*


@Composable
fun StatsScreen() {
    val tViewModel = TransactionViewModel(
        TransactionRepository(
            DB.getInstance(LocalContext.current).TransactionDao()
        )
    )

    var now by rememberSaveable { mutableStateOf(LocalDate.now()) }

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
        PieChart(tViewModel, now)
    }
}
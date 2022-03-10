package com.example.moneymanager.ui

import com.example.moneymanager.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Transactions : NavigationItem("transactions", R.drawable.ic_twotone_book_24, "Transactions")
    object Stats : NavigationItem("stats", R.drawable.ic_twotone_bar_chart_24, "Stats")
    object Accounts : NavigationItem("accounts", R.drawable.ic_twotone_account_balance_wallet_24, "Accounts")
}

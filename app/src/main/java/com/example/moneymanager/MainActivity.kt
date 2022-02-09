package com.example.moneymanager

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.theme.MoneyManagerTheme
import com.example.moneymanager.ui.viewmodel.MainViewModel
import com.example.moneymanager.ui.views.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    companion object {
        private lateinit var mainViewModel: MainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firstLaunch: Boolean

        mainViewModel = MainViewModel(application)

        val prefGet = getSharedPreferences("Preferences", Activity.MODE_PRIVATE)
        firstLaunch = prefGet.getBoolean("isFirstLaunch", true)

        if (firstLaunch) {
            mainViewModel.insertAccount(Account(0, "Bank", "Nordea", 100f, true))

            val prefPut = getSharedPreferences("Preferences", Activity.MODE_PRIVATE)
            val prefEditor = prefPut.edit()
            prefEditor.putBoolean("isFirstLaunch", false)
            prefEditor.apply()
        }


        setContent {
            //Sets UI Bar as same color as background
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }

            MoneyManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { addTransaction() },
                    //When navigation route is on Transactions, set FAB size to 48dp, else 0
                    modifier =
                    when (navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route) {
                        NavigationItem.Transactions.route -> Modifier.size(48.dp)
                        else -> {
                            Modifier.size(0.dp)
                        }
                    }
                )
                {
                    Icon(Icons.TwoTone.Add, contentDescription = "Add transaction")
                }
            }
        ) {
            Navigation(navController)
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Transactions,
            NavigationItem.Stats,
            NavigationItem.Accounts,
            NavigationItem.Settings
        )
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) },
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = MaterialTheme.colors.onBackground,
                    alwaysShowLabel = false,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Transactions.route) {
            composable(NavigationItem.Transactions.route) {
                TransactionScreen(mainViewModel)
            }
            composable(NavigationItem.Stats.route) {
                StatsScreen()
            }
            composable(NavigationItem.Accounts.route) {
                AccountsScreen()
            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}


//Converting Long to Date and other way around
//fun convertLongToTime(time: Long): String {
//    val date = Date(time)
//    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
//    return format.format(date)
//}
//
//fun currentTimeToLong(): Long {
//    return System.currentTimeMillis()
//}
//
//fun convertDateToLong(date: String): Long {
//    val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
//    return df.parse(date).time
//}

fun nig() {

}
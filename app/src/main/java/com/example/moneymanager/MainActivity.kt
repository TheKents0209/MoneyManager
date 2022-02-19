package com.example.moneymanager

import InsertTransaction
import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.runtime.*
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
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.model.Transaction
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.theme.MoneyManagerTheme
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.SensorViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.example.moneymanager.ui.views.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.squareup.seismic.ShakeDetector

class MainActivity : ComponentActivity(), ShakeDetector.Listener {

    private val sViewModel = SensorViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sd = ShakeDetector(this)
        sd.setSensitivity(11)
        sd.start(sensorManager)

        val firstLaunch: Boolean

        val prefGet = getSharedPreferences("Preferences", Activity.MODE_PRIVATE)
        firstLaunch = prefGet.getBoolean("isFirstLaunch", true)

        if (firstLaunch) {
            val accountViewModel =
                AccountViewModel(AccountRepository(DB.getInstance(application).AccountDao()))
            val transactionViewModel = TransactionViewModel(
                TransactionRepository(
                    DB.getInstance(application).TransactionDao()
                )
            )
            accountViewModel.insertAccount(Account(0, "Bank", "Nordea", 10000, true))
            transactionViewModel.insertTransaction(
                Transaction(
                    0,
                    -1,
                    "2022-02-16",
                    "Food",
                    1,
                    2000,
                    "",
                    ""
                )
            )

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
        val currentRouteDestination =
            navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route
        val isShaken by sViewModel.isShaken.collectAsState()
        LaunchedEffect(isShaken) {
            if (isShaken && currentRouteDestination != "AddTransaction") {
                navController.navigate("AddTransaction")
            }
        }
        Scaffold(
            topBar = {
                if (currentRouteDestination == "AddTransaction") {
                    TopAppBar(
                        title = { Text(text = "Transaction") },
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.navigateUp()
                            }) {
                                Icon(
                                    painterResource(R.drawable.ic_twotone_chevron_left_24),
                                    contentDescription = "Previous month"
                                )
                            }
                        },
                        backgroundColor = MaterialTheme.colors.background
                    )
                }
            },
            bottomBar = {
                if (currentRouteDestination != "AddTransaction") {
                    BottomNavigationBar(navController)
                }
                if (currentRouteDestination == "AddTransaction") {
                    InsertTransaction(navController)
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("AddTransaction") },
                    //When navigation route is on Transactions, set FAB size to 48dp, else 0
                    modifier =
                    when (currentRouteDestination) {
                        NavigationItem.Transactions.route -> Modifier.size(48.dp)
                        //Dirty way, but navigation route is null only on startup anyway
                        null -> Modifier.size(48.dp)
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
                TransactionScreen()
                sViewModel.reset()
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
            composable("AddTransaction") {
                InsertTransaction(navController)
            }
        }
    }

    override fun hearShake() {
        sViewModel.shake()
    }
}
package com.example.moneymanager

import android.app.Activity
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.ui.NavigationItem
import com.example.moneymanager.ui.theme.MoneyManagerTheme
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.SensorViewModel
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

            //Creating basic accounts so user doesn't need to add them themselves "QOL"
            accountViewModel.insertAccount(Account(0, "Cash", "Cash", 0, true))
            accountViewModel.insertAccount(Account(0, "Bank", "Bank", 0, true))
            accountViewModel.insertAccount(Account(0, "Savings", "Savings", 0, true))
            accountViewModel.insertAccount(Account(0, "Investments", "Investments", 0, true))

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
            if (isShaken && currentRouteDestination != "addTransaction") {
                navController.navigate("addTransaction")
            }
        }
        Scaffold(
            topBar = {
                if (currentRouteDestination != null) {
                    if (currentRouteDestination == "addTransaction" || currentRouteDestination.startsWith("editTransaction")) {
                        TopAppBar(
                            title = { Text(resources.getQuantityString(R.plurals.transaction, 1)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        painterResource(R.drawable.ic_twotone_chevron_left_24),
                                        contentDescription = "Previous screen"
                                    )
                                }
                            },
                            backgroundColor = MaterialTheme.colors.background
                        )
                    }
                    if (currentRouteDestination == "addAccount") {
                        TopAppBar(
                            title = { Text(stringResource(R.string.accounts)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        painterResource(R.drawable.ic_twotone_chevron_left_24),
                                        contentDescription = "Previous screen"
                                    )
                                }
                            },
                            backgroundColor = MaterialTheme.colors.background
                        )
                    }
                }
            },
            bottomBar = {
                if (currentRouteDestination != null) {
                    if (currentRouteDestination != "addTransaction" &&
                        !currentRouteDestination.startsWith("editTransaction") &&
                        currentRouteDestination != "addAccount"
                    ) {
                        BottomNavigationBar(navController)
                    }
                }
            },
            floatingActionButton = {
                if (currentRouteDestination == "transactions" || currentRouteDestination == null) {
                    FloatingActionButton(onClick = { navController.navigate("addTransaction") })
                    {
                        Icon(Icons.TwoTone.Add, contentDescription = "Add transaction")
                    }
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
                TransactionScreen(navController)
                sViewModel.reset()
            }
            composable(NavigationItem.Stats.route) {
                StatsScreen()
            }
            composable(NavigationItem.Accounts.route) {
                AccountsScreen(navController)
            }
            composable("addTransaction") {
                InsertTransaction(navController)
            }
            composable(
                "editTransaction/{transactionId}",
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                EditTransaction(backStackEntry.arguments?.getLong("transactionId"), navController)
            }
            composable("addAccount") {
                InsertAccount(navController)
            }
        }
    }

    override fun hearShake() {
        sViewModel.shake()
    }
}
package com.prunance.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prunance.app.data.repository.FinanceRepository
import com.prunance.app.navigation.BottomNavItem
import com.prunance.app.ui.components.AddExpenseSheet
import com.prunance.app.ui.screens.analysis.AnalysisScreen
import com.prunance.app.ui.screens.ledger.LedgerScreen
import com.prunance.app.ui.screens.onboarding.OnboardingScreen
import com.prunance.app.ui.screens.pulse.PulseScreen
import com.prunance.app.ui.screens.strategy.StrategyScreen
import com.prunance.app.ui.theme.PrunanceTheme
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PrunanceTheme {
                PrunanceRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrunanceRoot() {
    val context = LocalContext.current
    val repository = remember { FinanceRepository(context) }

    val hasCompletedOnboarding by repository.prefs.hasCompletedOnboarding
        .collectAsStateWithLifecycle(initialValue = null)

    // Wait for DataStore to load before showing anything
    when (hasCompletedOnboarding) {
        null -> {
            // Loading state — just show background
        }
        false -> {
            OnboardingScreen(
                onComplete = {
                    // The state will automatically update via the Flow
                }
            )
        }
        true -> {
            PrunanceApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrunanceApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { FinanceRepository(context) }
    val currency by repository.prefs.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val coroutineScope = rememberCoroutineScope()

    var showExpenseSheet by remember { mutableStateOf(false) }

    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PrunanceBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showExpenseSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Pulse.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.Pulse.route) { 
                PulseScreen(onNavigateToSettings = { navController.navigate("settings") }) 
            }
            composable(BottomNavItem.Ledger.route) { LedgerScreen() }
            composable(BottomNavItem.Strategy.route) { 
                StrategyScreen(onNavigateToGoalDetail = { goalId -> navController.navigate("goal_detail/$goalId") }) 
            }
            composable(BottomNavItem.Analysis.route) { AnalysisScreen() }
            composable("settings") { 
                com.prunance.app.ui.screens.settings.SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("goal_detail/{goalId}") { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: return@composable
                val goalViewModel = remember { 
                    com.prunance.app.ui.screens.goals.GoalDetailViewModel(repository, goalId) 
                }
                com.prunance.app.ui.screens.goals.GoalDetailScreen(
                    viewModel = goalViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    // ── Expense entry bottom sheet ────────────────────────────────
    if (showExpenseSheet) {
        AddExpenseSheet(
            currencySymbol = currencySymbol,
            onDismiss = { showExpenseSheet = false },
            onSave = { expense ->
                coroutineScope.launch {
                    repository.addExpense(expense)
                }
            }
        )
    }
}

@Composable
private fun PrunanceBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        BottomNavItem.items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(BottomNavItem.Pulse.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

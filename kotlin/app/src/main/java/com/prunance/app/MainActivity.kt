package com.prunance.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.screens.analysis.AnalysisScreen
import com.prunance.app.ui.screens.ledger.LedgerScreen
import com.prunance.app.ui.screens.splash.SplashScreen
import com.prunance.app.ui.screens.splash.SplashState
import com.prunance.app.ui.screens.splash.SplashViewModel
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

@Composable
fun PrunanceRoot() {
    val splashViewModel: SplashViewModel = viewModel()
    val splashState by splashViewModel.splashState.collectAsStateWithLifecycle()

    when (splashState) {
        is SplashState.Loading -> {
            SplashScreen()
        }
        is SplashState.OnboardingRequired -> {
            OnboardingScreen(
                onComplete = {
                    // After onboarding, transition to AppReady by updating splash state
                    splashViewModel.completeOnboardingTransition()
                }
            )
        }
        is SplashState.AppReady -> {
            PrunanceApp()
        }
    }
}

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Pulse.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp) // Space for bottom bar
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

        // FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 104.dp, end = 24.dp)
        ) {
            GlassFab(onClick = { showExpenseSheet = true })
        }

        // Bottom Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            PrunanceBottomBar(navController)
        }
    }

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
fun GlassFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(PrunanceTheme.colors.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "Add",
            colorFilter = ColorFilter.tint(Color.Black)
        )
    }
}

@Composable
private fun PrunanceBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        cornerRadius = 32.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.items.forEach { item ->
                val selected = currentRoute == item.route
                val contentColor = if (selected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textSecondary

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(BottomNavItem.Pulse.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberVectorPainter(if (selected) item.filledIcon else item.outlinedIcon),
                        contentDescription = item.label,
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                    if (selected) {
                        PrunanceText(
                            text = item.label,
                            style = PrunanceTheme.typography.labelSmall,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

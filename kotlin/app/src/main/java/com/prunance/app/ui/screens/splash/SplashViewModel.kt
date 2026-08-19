package com.prunance.app.ui.screens.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashState {
    object Loading : SplashState()
    object OnboardingRequired : SplashState()
    object AppReady : SplashState()
}

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState

    init {
        // Launch in viewModelScope to ensure coroutine is tied to ViewModel lifecycle
        viewModelScope.launch {
            try {
                // Ensure DataStore reads are complete
                val hasCompletedOnboarding = repository.prefs.hasCompletedOnboarding.first()

                if (hasCompletedOnboarding) {
                    _splashState.value = SplashState.AppReady
                } else {
                    _splashState.value = SplashState.OnboardingRequired
                }
            } catch (e: Exception) {
                android.util.Log.e("SplashViewModel", "Error reading DataStore during initialization", e)
                // Report to analytics placeholder: Analytics.logError(e)
                // For now, handle by treating as OnboardingRequired to avoid infinite load or crash
                _splashState.value = SplashState.OnboardingRequired
            }
        }
    }

    fun completeOnboardingTransition() {
        // This function is called from MainActivity after onboarding is completed
        // to explicitly signal the SplashViewModel to transition to AppReady.
        viewModelScope.launch {
            _splashState.value = SplashState.AppReady
        }
    }
}

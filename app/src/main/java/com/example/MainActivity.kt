package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.CubePaymentViewModel
import com.example.ui.Screen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BuyerPaymentScreen
import com.example.ui.screens.CreateCubeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PaymentReceiptScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SecurityCenterScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CubePaymentTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: CubePaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle potential deep links or shared links
        intent?.data?.let { uri ->
            val path = uri.path
            if (path != null && path.contains("/c/")) {
                val cubeId = path.substringAfter("/c/")
                if (cubeId.isNotBlank()) {
                    viewModel.navigateTo(Screen.BuyerPayment(cubeId))
                }
            }
        }

        setContent {
            CubePaymentTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val currentScreen by viewModel.currentScreen.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.snackBarMessage.collectLatest { msg ->
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                // Hardware back press handler
                BackHandler(enabled = currentScreen !is Screen.Home) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = BackgroundDark,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(BackgroundDark)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(viewModel = viewModel)
                            }
                            is Screen.Auth -> {
                                AuthScreen(
                                    viewModel = viewModel,
                                    initialIsSignUp = screen.isSignUp
                                )
                            }
                            is Screen.Dashboard -> {
                                DashboardScreen(viewModel = viewModel)
                            }
                            is Screen.CreateCube -> {
                                CreateCubeScreen(
                                    viewModel = viewModel,
                                    existingCubeId = screen.existingCubeId
                                )
                            }
                            is Screen.BuyerPayment -> {
                                BuyerPaymentScreen(
                                    cubeId = screen.cubeId,
                                    viewModel = viewModel
                                )
                            }
                            is Screen.PaymentReceipt -> {
                                PaymentReceiptScreen(
                                    transaction = screen.transaction,
                                    viewModel = viewModel
                                )
                            }
                            is Screen.Store -> {
                                StoreScreen(viewModel = viewModel)
                            }
                            is Screen.Profile -> {
                                ProfileScreen(viewModel = viewModel)
                            }
                            is Screen.SecurityCenter -> {
                                SecurityCenterScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

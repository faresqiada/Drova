package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.BuildConfig
import com.example.core.di.ServiceLocator
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.UserRole
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.admin.AdminDashboardScreen
import com.example.presentation.admin.AdminViewModel
import com.example.presentation.auth.LoginScreen
import com.example.presentation.auth.PhoneOtpScreen
import com.example.presentation.auth.RegisterScreen
import com.example.presentation.captain.CaptainDashboardScreen
import com.example.presentation.captain.CaptainViewModel
import com.example.presentation.customer.CustomerHomeScreen
import com.example.presentation.customer.CustomerViewModel
import com.example.presentation.customer.feedback.CustomerFeedbackScreen
import com.example.presentation.restaurant.RestaurantDashboardScreen
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.presentation.support.ContactUsScreen
import com.example.presentation.roleselection.RoleSelectionScreen
import com.example.presentation.splash.SplashScreen
import com.example.presentation.welcome.WelcomeScreen

@Composable
fun DrovaNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    customerViewModel: CustomerViewModel = viewModel(),
    restaurantViewModel: RestaurantViewModel = viewModel(),
    captainViewModel: CaptainViewModel = viewModel()
) {
    val selectedRole by authViewModel.selectedRole.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.RoleSelection.route)
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                currentSelectedRole = selectedRole,
                onRoleSelected = { role ->
                    authViewModel.selectRole(role)
                },
                onContinueClick = {
                    navController.navigate(Screen.Login.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    val destination = RoleDestination.routeFor(role)
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onOtpClick = {
                    navController.navigate(Screen.PhoneOtp.route)
                },
                onChangeRoleClick = {
                    navController.navigate(Screen.RoleSelection.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PhoneOtp.route) {
            PhoneOtpScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    val destination = RoleDestination.routeFor(role)
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ContactUs.route) {
            ContactUsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.CustomerFeedback.route) {
            CustomerFeedbackScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { role ->
                    val destination = RoleDestination.routeFor(role)
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onChangeRoleClick = {
                    navController.navigate(Screen.RoleSelection.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                customerViewModel = customerViewModel,
                onRoleSwitch = { /* Role changes require a verified re-authenticated session. */ },
                onContactUs = { navController.navigate(Screen.ContactUs.route) },
                onFeedback = { navController.navigate(Screen.CustomerFeedback.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RestaurantDashboard.route) {
            RestaurantDashboardScreen(
                restaurantViewModel = restaurantViewModel,
                onRoleSwitch = { /* Role changes require a verified re-authenticated session. */ },
                onContactUs = { navController.navigate(Screen.ContactUs.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CaptainDashboard.route) {
            CaptainDashboardScreen(
                captainViewModel = captainViewModel,
                onRoleSwitch = { /* Role changes require a verified re-authenticated session. */ },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            val adminViewModel: AdminViewModel = viewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

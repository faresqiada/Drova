package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.UserRole
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.auth.LoginScreen
import com.example.presentation.auth.RegisterScreen
import com.example.presentation.captain.CaptainDashboardScreen
import com.example.presentation.captain.CaptainViewModel
import com.example.presentation.customer.CustomerHomeScreen
import com.example.presentation.customer.CustomerViewModel
import com.example.presentation.restaurant.RestaurantDashboardScreen
import com.example.presentation.restaurant.RestaurantViewModel
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
                currentSelectedRole = authViewModel.selectedRole.value,
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
                    val destination = when (role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.RESTAURANT -> Screen.RestaurantDashboard.route
                        UserRole.CAPTAIN -> Screen.CaptainDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onChangeRoleClick = {
                    navController.navigate(Screen.RoleSelection.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { role ->
                    val destination = when (role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.RESTAURANT -> Screen.RestaurantDashboard.route
                        UserRole.CAPTAIN -> Screen.CaptainDashboard.route
                    }
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
                onRoleSwitch = { newRole ->
                    authViewModel.quickSwitchRole(newRole)
                    when (newRole) {
                        UserRole.CUSTOMER -> {}
                        UserRole.RESTAURANT -> navController.navigate(Screen.RestaurantDashboard.route) {
                            popUpTo(Screen.CustomerHome.route) { inclusive = true }
                        }
                        UserRole.CAPTAIN -> navController.navigate(Screen.CaptainDashboard.route) {
                            popUpTo(Screen.CustomerHome.route) { inclusive = true }
                        }
                    }
                },
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
                onRoleSwitch = { newRole ->
                    authViewModel.quickSwitchRole(newRole)
                    when (newRole) {
                        UserRole.RESTAURANT -> {}
                        UserRole.CUSTOMER -> navController.navigate(Screen.CustomerHome.route) {
                            popUpTo(Screen.RestaurantDashboard.route) { inclusive = true }
                        }
                        UserRole.CAPTAIN -> navController.navigate(Screen.CaptainDashboard.route) {
                            popUpTo(Screen.RestaurantDashboard.route) { inclusive = true }
                        }
                    }
                },
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
                onRoleSwitch = { newRole ->
                    authViewModel.quickSwitchRole(newRole)
                    when (newRole) {
                        UserRole.CAPTAIN -> {}
                        UserRole.CUSTOMER -> navController.navigate(Screen.CustomerHome.route) {
                            popUpTo(Screen.CaptainDashboard.route) { inclusive = true }
                        }
                        UserRole.RESTAURANT -> navController.navigate(Screen.RestaurantDashboard.route) {
                            popUpTo(Screen.CaptainDashboard.route) { inclusive = true }
                        }
                    }
                },
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

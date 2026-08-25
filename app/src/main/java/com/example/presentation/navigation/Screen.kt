package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object RoleSelection : Screen("role_selection")
    object Login : Screen("login")
    object PhoneOtp : Screen("phone_otp")
    object ContactUs : Screen("contact_us")
    object Register : Screen("register")

    // Role-specific main entry screens
    object CustomerHome : Screen("customer_home")
    object RestaurantDashboard : Screen("restaurant_dashboard")
    object CaptainDashboard : Screen("captain_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
}

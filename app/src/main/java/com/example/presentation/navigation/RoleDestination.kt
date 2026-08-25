package com.example.presentation.navigation

import com.example.domain.model.UserRole

/**
 * Single authorization-aware navigation mapping used after a successful login.
 * The role is supplied by the authenticated profile/claims, never by UI text.
 */
object RoleDestination {
    fun routeFor(role: UserRole): String = when (role) {
        UserRole.CUSTOMER -> Screen.CustomerHome.route
        UserRole.RESTAURANT -> Screen.RestaurantDashboard.route
        UserRole.CAPTAIN -> Screen.CaptainDashboard.route
        UserRole.ADMIN -> Screen.AdminDashboard.route
    }
}

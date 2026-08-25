package com.example

import com.example.domain.model.UserRole
import com.example.presentation.navigation.RoleDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class RoleDestinationTest {
    @Test
    fun `each authenticated role resolves to its own dashboard route`() {
        val customer = RoleDestination.routeFor(UserRole.CUSTOMER)
        val restaurant = RoleDestination.routeFor(UserRole.RESTAURANT)
        val captain = RoleDestination.routeFor(UserRole.CAPTAIN)
        val admin = RoleDestination.routeFor(UserRole.ADMIN)

        assertEquals("customer_home", customer)
        assertEquals("restaurant_dashboard", restaurant)
        assertEquals("captain_dashboard", captain)
        assertEquals("admin_dashboard", admin)
        assertNotEquals(customer, restaurant)
        assertNotEquals(customer, captain)
        assertNotEquals(restaurant, captain)
    }
}

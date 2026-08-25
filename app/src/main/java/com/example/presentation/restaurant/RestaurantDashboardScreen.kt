package com.example.presentation.restaurant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.UserRole
import com.example.presentation.restaurant.dashboard.RestaurantDashboardTab
import com.example.presentation.restaurant.finance.RestaurantFinanceTab
import com.example.presentation.restaurant.menu.RestaurantMenuTab
import com.example.presentation.restaurant.orders.RestaurantOrdersTab
import com.example.presentation.restaurant.profile.RestaurantProfileTab
import com.example.ui.theme.*

@Composable
fun RestaurantDashboardScreen(
    restaurantViewModel: RestaurantViewModel,
    onRoleSwitch: (UserRole) -> Unit,
    onContactUs: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentUser by restaurantViewModel.currentUser.collectAsState()
    val restaurant by restaurantViewModel.restaurantData.collectAsState()
    val selectedTab by restaurantViewModel.selectedTab.collectAsState()

    val pendingActionsCount by restaurantViewModel.pendingActionsCount.collectAsState()
    val activeOrdersCount by restaurantViewModel.activeOrdersCount.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_dashboard_screen"),
        topBar = {
            Surface(
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DrovaMark(size = 32.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = restaurant?.nameAr ?: (currentUser?.businessName ?: "شاورما الريم"),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (restaurant?.isOpen == true) DrovaSuccess else DrovaError)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (restaurant?.isOpen == true)
                                            (if (isAr) "متاح للطلب" else "Open")
                                        else (if (isAr) "مغلق مؤقتاً" else "Closed"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (restaurant?.isOpen == true) DrovaSuccessText else DrovaErrorText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DrovaRoleBadge(role = UserRole.RESTAURANT)
                            Spacer(modifier = Modifier.width(8.dp))
                            DrovaLanguageToggle()
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("restaurant_logout_topbar")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "خروج",
                                    tint = DrovaTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = DrovaSurface,
                contentColor = DrovaTurquoise,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("restaurant_bottom_nav")
            ) {
                RestaurantMainTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val badgeCount = when (tab) {
                        RestaurantMainTab.ORDERS -> activeOrdersCount
                        RestaurantMainTab.DASHBOARD -> pendingActionsCount
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { restaurantViewModel.selectTab(tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = if (tab == RestaurantMainTab.DASHBOARD) DrovaWarning else DrovaTurquoise,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "$badgeCount",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = getTabIcon(tab, isSelected),
                                    contentDescription = if (isAr) tab.titleAr else tab.titleEn
                                )
                            }
                        },
                        label = {
                            Text(
                                text = if (isAr) tab.titleAr else tab.titleEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DrovaTurquoise,
                            selectedTextColor = DrovaTurquoise,
                            indicatorColor = DrovaTurquoiseLight,
                            unselectedIconColor = DrovaTextSecondary,
                            unselectedTextColor = DrovaTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                RestaurantMainTab.DASHBOARD -> {
                    RestaurantDashboardTab(
                        restaurantViewModel = restaurantViewModel
                    )
                }
                RestaurantMainTab.ORDERS -> {
                    RestaurantOrdersTab(
                        restaurantViewModel = restaurantViewModel
                    )
                }
                RestaurantMainTab.MENU -> {
                    RestaurantMenuTab(
                        restaurantViewModel = restaurantViewModel
                    )
                }
                RestaurantMainTab.FINANCE -> {
                    RestaurantFinanceTab(
                        restaurantViewModel = restaurantViewModel
                    )
                }
                RestaurantMainTab.PROFILE -> {
                    RestaurantProfileTab(
                        restaurantViewModel = restaurantViewModel,
                        onRoleSwitch = onRoleSwitch,
                        onContactUs = onContactUs,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

private fun getTabIcon(tab: RestaurantMainTab, isSelected: Boolean): ImageVector {
    return when (tab) {
        RestaurantMainTab.DASHBOARD -> if (isSelected) Icons.Default.Dashboard else Icons.Outlined.Dashboard
        RestaurantMainTab.ORDERS -> if (isSelected) Icons.Default.RestaurantMenu else Icons.Outlined.RestaurantMenu
        RestaurantMainTab.MENU -> if (isSelected) Icons.Default.MenuBook else Icons.Outlined.MenuBook
        RestaurantMainTab.FINANCE -> if (isSelected) Icons.Default.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
        RestaurantMainTab.PROFILE -> if (isSelected) Icons.Default.Storefront else Icons.Outlined.Storefront
    }
}

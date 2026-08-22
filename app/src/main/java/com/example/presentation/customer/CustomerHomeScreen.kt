package com.example.presentation.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.UserRole
import com.example.presentation.customer.cart.CartScreen
import com.example.presentation.customer.checkout.CheckoutScreen
import com.example.presentation.customer.home.CustomerHomeScreenContent
import com.example.presentation.customer.orders.CustomerOrdersScreen
import com.example.presentation.customer.orders.OrderDetailScreen
import com.example.presentation.customer.orders.OrderTrackingScreen
import com.example.presentation.customer.product.ProductDetailModal
import com.example.presentation.customer.profile.CustomerProfileScreen
import com.example.presentation.customer.restaurant.RestaurantDetailScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    customerViewModel: CustomerViewModel,
    onRoleSwitch: (UserRole) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentTab by customerViewModel.currentTab.collectAsState()
    val currentDestination by customerViewModel.currentDestination.collectAsState()
    val currentUser by customerViewModel.currentUser.collectAsState()
    val selectedAddress by customerViewModel.selectedAddress.collectAsState()
    val savedAddresses by customerViewModel.savedAddresses.collectAsState()
    val cartItemCount by customerViewModel.cartItemCount.collectAsState()
    val cartSubtotal by customerViewModel.cartSubtotalEgp.collectAsState()
    val activeOrders by customerViewModel.activeCustomerOrders.collectAsState()

    // Sub-screens state
    val activeRestaurant by customerViewModel.activeRestaurant.collectAsState()
    val productDetailItem by customerViewModel.productDetailItem.collectAsState()
    val productDetailRestaurant by customerViewModel.productDetailRestaurant.collectAsState()
    val trackingOrder by customerViewModel.currentTrackingOrder.collectAsState()
    val conflictItemPending by customerViewModel.conflictItemPending.collectAsState()

    var showAddressSelectorSheet by remember { mutableStateOf(false) }

    // Intercept back button when not on main screen
    BackHandler(enabled = currentDestination !is CustomerDestination.Main) {
        customerViewModel.navigateBack()
    }

    // Main Scaffold Layout
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_root_screen"),
        topBar = {
            if (currentDestination is CustomerDestination.Main) {
                CustomerMainTopBar(
                    selectedAddressLabel = selectedAddress.labelAr,
                    selectedAddressDistrict = selectedAddress.districtAr,
                    cartItemCount = cartItemCount,
                    onAddressClick = { showAddressSelectorSheet = true },
                    onCartClick = { customerViewModel.navigateToCart() }
                )
            }
        },
        bottomBar = {
            if (currentDestination is CustomerDestination.Main) {
                CustomerBottomNavigationBar(
                    currentTab = currentTab,
                    activeOrdersCount = activeOrders.size,
                    onTabSelected = { customerViewModel.selectTab(it) }
                )
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentDestination) {
                is CustomerDestination.Main -> {
                    when (currentTab) {
                        CustomerTab.HOME -> {
                            CustomerHomeScreenContent(
                                viewModel = customerViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        CustomerTab.ORDERS -> {
                            CustomerOrdersScreen(
                                viewModel = customerViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        CustomerTab.PROFILE -> {
                            CustomerProfileScreen(
                                user = currentUser,
                                viewModel = customerViewModel,
                                onSwitchRole = onRoleSwitch,
                                onLogout = onLogout,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Floating Cart Pill on Home tab if items exist in cart
                    if (currentTab == CustomerTab.HOME && cartItemCount > 0) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("floating_cart_pill"),
                            shape = RoundedCornerShape(12.dp),
                            color = DrovaDeep,
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { customerViewModel.navigateToCart() }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = DrovaPrimary
                                    ) {
                                        Text(
                                            text = "$cartItemCount",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isAr) "سلة المشتريات" else "View Basket",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${cartSubtotal} ج.م",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = if (isAr) "عرض السلة ←" else "View Cart →",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaCyanAccent,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }

                is CustomerDestination.RestaurantDetail -> {
                    val restaurant = activeRestaurant ?: customerViewModel.restaurants.value.find { it.id == dest.restaurantId }
                    if (restaurant != null) {
                        RestaurantDetailScreen(
                            restaurant = restaurant,
                            viewModel = customerViewModel
                        )
                    }
                }

                is CustomerDestination.Cart -> {
                    CartScreen(viewModel = customerViewModel)
                }

                is CustomerDestination.Checkout -> {
                    CheckoutScreen(viewModel = customerViewModel)
                }

                is CustomerDestination.OrderTracking -> {
                    val order = trackingOrder ?: customerViewModel.allOrders.value.find { it.id == dest.orderId }
                    if (order != null) {
                        OrderTrackingScreen(
                            order = order,
                            viewModel = customerViewModel
                        )
                    }
                }

                is CustomerDestination.OrderDetail -> {
                    val order = trackingOrder ?: customerViewModel.allOrders.value.find { it.id == dest.orderId }
                    if (order != null) {
                        OrderDetailScreen(
                            order = order,
                            viewModel = customerViewModel
                        )
                    }
                }
            }
        }
    }

    // Product Detail Bottom Sheet Modal
    if (productDetailItem != null && productDetailRestaurant != null) {
        ProductDetailModal(
            item = productDetailItem!!,
            restaurant = productDetailRestaurant!!,
            onDismiss = { customerViewModel.closeProductDetails() },
            onAddToCart = { qty, notes ->
                customerViewModel.addToCart(productDetailRestaurant!!, productDetailItem!!, qty, notes)
            }
        )
    }

    // Cart Conflict Dialog
    if (conflictItemPending != null) {
        val pendingRest = conflictItemPending!!.first
        AlertDialog(
            onDismissRequest = { customerViewModel.dismissCartConflict() },
            title = {
                Text(
                    text = if (isAr) "إفراغ السلة الحالية؟" else "Clear current basket?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = if (isAr)
                        "تحتوي سلتك على طلبات من مطعم آخر. يمكنك الطلب من مطعم واحد فقط في المرة الواحدة. هل تريد إفراغ السلة والبدء مع \"${pendingRest.nameAr}\"؟"
                    else
                        "Your basket has items from another restaurant. Clear it to start ordering from ${pendingRest.nameEn}?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { customerViewModel.confirmClearCartAndAddNewItem() },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaPrimary),
                    modifier = Modifier.testTag("confirm_cart_clear_btn")
                ) {
                    Text(text = if (isAr) "إفراغ والطلب من ${pendingRest.nameAr}" else "Clear & Order", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { customerViewModel.dismissCartConflict() },
                    modifier = Modifier.testTag("dismiss_cart_clear_btn")
                ) {
                    Text(text = if (isAr) "إلغاء والاحتفاظ بالسلة" else "Cancel")
                }
            }
        )
    }

    // Address Selector Sheet
    if (showAddressSelectorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSelectorSheet = false },
            containerColor = DrovaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = if (isAr) "توصيل الطلب إلى" else "Deliver to",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaDeep
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                savedAddresses.forEach { addr ->
                    val isSelected = addr.id == selectedAddress.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) DrovaTurquoiseLight else DrovaSurface,
                        border = BorderStroke(1.dp, if (isSelected) DrovaTurquoise else DrovaBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                customerViewModel.selectAddress(addr)
                                showAddressSelectorSheet = false
                            }
                            .testTag("home_address_option_${addr.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) DrovaPrimary else DrovaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = addr.labelAr,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "${addr.detailedAddressAr} - ${addr.districtAr}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DrovaOutlinedButton(
                    text = if (isAr) "إغلاق" else "Close",
                    onClick = { showAddressSelectorSheet = false }
                )
            }
        }
    }
}

@Composable
private fun CustomerMainTopBar(
    selectedAddressLabel: String,
    selectedAddressDistrict: String,
    cartItemCount: Int,
    onAddressClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier.fillMaxWidth(),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Address Selector Trigger
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onAddressClick)
                    .testTag("topbar_address_selector"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DrovaTurquoiseLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DrovaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${if (isAr) "التوصيل إلى:" else "Deliver to:"} $selectedAddressLabel",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaDeep,
                                fontSize = 13.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = DrovaTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = selectedAddressDistrict,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Right Actions: Language Toggle + Cart Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DrovaLanguageToggle()

                Box {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("topbar_cart_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "السلة",
                            tint = DrovaDeep,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (cartItemCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = DrovaPrimary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                        ) {
                            Text(
                                text = "$cartItemCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerBottomNavigationBar(
    currentTab: CustomerTab,
    activeOrdersCount: Int,
    onTabSelected: (CustomerTab) -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    NavigationBar(
        containerColor = DrovaSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = currentTab == CustomerTab.HOME,
            onClick = { onTabSelected(CustomerTab.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = if (isAr) "الرئيسية" else "Home"
                )
            },
            label = {
                Text(
                    text = if (isAr) "الرئيسية" else "Home",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DrovaPrimary,
                selectedTextColor = DrovaPrimary,
                indicatorColor = DrovaTurquoiseLight,
                unselectedIconColor = DrovaTextSecondary,
                unselectedTextColor = DrovaTextSecondary
            ),
            modifier = Modifier.testTag("tab_home")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.ORDERS,
            onClick = { onTabSelected(CustomerTab.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(
                                containerColor = DrovaPrimary,
                                contentColor = Color.White
                            ) {
                                Text("$activeOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = if (isAr) "طلباتي" else "Orders"
                    )
                }
            },
            label = {
                Text(
                    text = if (isAr) "طلباتي" else "Orders",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DrovaPrimary,
                selectedTextColor = DrovaPrimary,
                indicatorColor = DrovaTurquoiseLight,
                unselectedIconColor = DrovaTextSecondary,
                unselectedTextColor = DrovaTextSecondary
            ),
            modifier = Modifier.testTag("tab_orders")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.PROFILE,
            onClick = { onTabSelected(CustomerTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = if (isAr) "حسابي" else "Profile"
                )
            },
            label = {
                Text(
                    text = if (isAr) "حسابي" else "Profile",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DrovaPrimary,
                selectedTextColor = DrovaPrimary,
                indicatorColor = DrovaTurquoiseLight,
                unselectedIconColor = DrovaTextSecondary,
                unselectedTextColor = DrovaTextSecondary
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}

package com.example.presentation.customer.restaurant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.MenuItem
import com.example.domain.model.Restaurant
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val cartItems by viewModel.cartItems.collectAsState()
    val cartRestaurant by viewModel.cartRestaurant.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotalEgp.collectAsState()

    // Menu category filter
    val menuCategories = remember(restaurant.menu) {
        listOf("الكل") + restaurant.menu.map { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filteredMenuItems = remember(selectedCategory, restaurant.menu) {
        if (selectedCategory == "الكل") {
            restaurant.menu
        } else {
            restaurant.menu.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DrovaBackground)
            .testTag("restaurant_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cartItemCount > 0) 100.dp else 32.dp)
        ) {
            // Top App Bar / Header
            item {
                RestaurantHeaderSection(
                    restaurant = restaurant,
                    onBackClick = { viewModel.navigateBack() }
                )
            }

            // Menu Category Selector Chips
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isAr) "قائمة الطعام" else "Menu Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        menuCategories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) DrovaPrimary else DrovaSurface,
                                border = BorderStroke(1.dp, if (isSelected) DrovaPrimary else DrovaBorder),
                                modifier = Modifier
                                    .clickable { selectedCategory = cat }
                                    .testTag("menu_category_chip_$cat")
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else DrovaTextPrimary,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Menu Items List
            items(filteredMenuItems, key = { it.id }) { item ->
                val quantityInCart = cartItems.find { it.menuItem.id == item.id }?.quantity ?: 0

                MenuItemRowCard(
                    item = item,
                    quantityInCart = quantityInCart,
                    onItemClick = { viewModel.openProductDetails(item, restaurant) },
                    onQuickAdd = { viewModel.addToCart(restaurant, item, 1) },
                    onIncrement = { viewModel.updateCartItemQuantity(item.id, quantityInCart + 1) },
                    onDecrement = { viewModel.updateCartItemQuantity(item.id, quantityInCart - 1) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Sticky Bottom Cart Bar
        if (cartItemCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .testTag("restaurant_sticky_cart_bar"),
                shape = RoundedCornerShape(10.dp),
                color = DrovaDeep,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateToCart() }
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAr) "إتمام الطلب ←" else "Checkout →",
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
    }
}

@Composable
private fun RestaurantHeaderSection(
    restaurant: Restaurant,
    onBackClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier.fillMaxWidth(),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            // Top Navigation Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("restaurant_header_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = DrovaTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaTurquoiseLight
                ) {
                    Text(
                        text = restaurant.activeSubscriptionTier,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoiseHover,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                DrovaLanguageToggle()
            }

            // Restaurant Profile Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = restaurant.nameAr,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaDeep,
                                fontSize = 19.sp
                            )
                        )
                        Text(
                            text = restaurant.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaTurquoiseLight,
                        border = BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DrovaWarning,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${restaurant.rating}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "(${restaurant.reviewCount})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = restaurant.addressAr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Stats Bar (Time, Delivery fee, Min order)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatPill(
                        icon = Icons.Default.Timer,
                        label = if (isAr) "وقت التوصيل" else "Time",
                        value = "${restaurant.deliveryTimeMin} ${if (isAr) "دقيقة" else "min"}"
                    )

                    StatPill(
                        icon = Icons.Default.DeliveryDining,
                        label = if (isAr) "رسوم التوصيل" else "Fee",
                        value = "${restaurant.deliveryFeeEgp} ج.م"
                    )

                    StatPill(
                        icon = Icons.Default.ShoppingBag,
                        label = if (isAr) "الحد الأدنى" else "Min",
                        value = "${restaurant.minOrderEgp.toInt()} ج.م"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DrovaPrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaTextPrimary,
                    fontSize = 12.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DrovaTextSecondary,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun MenuItemRowCard(
    item: MenuItem,
    quantityInCart: Int,
    onItemClick: () -> Unit,
    onQuickAdd: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = item.isAvailable, onClick = onItemClick)
            .testTag("menu_item_${item.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.nameAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (item.isAvailable) DrovaTextPrimary else DrovaTextMuted,
                            fontSize = 14.sp
                        )
                    )
                    if (!item.isAvailable) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DrovaSurfaceVariant
                        ) {
                            Text(
                                text = if (isAr) "غير متاح" else "Unavailable",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DrovaError,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (item.descriptionAr.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.descriptionAr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${item.price} ج.م",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaPrimary,
                            fontSize = 14.sp
                        )
                    )

                    if (item.preparationTimeMin > 0) {
                        Text(
                            text = "• ${item.preparationTimeMin} ${if (isAr) "دقيقة تحضير" else "min prep"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Add to cart or quantity stepper
            if (item.isAvailable) {
                if (quantityInCart > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaSurfaceVariant,
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            IconButton(
                                onClick = onDecrement,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("item_decrement_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "تقليل",
                                    tint = DrovaPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Text(
                                text = "$quantityInCart",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = onIncrement,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("item_increment_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "زيادة",
                                    tint = DrovaPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaTurquoiseLight,
                        border = BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable(onClick = onQuickAdd)
                            .testTag("item_quick_add_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DrovaTurquoiseHover,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "إضافة" else "Add",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

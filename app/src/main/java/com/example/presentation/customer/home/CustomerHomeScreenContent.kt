package com.example.presentation.customer.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.designsystem.*
import com.example.domain.model.Order
import com.example.domain.model.Restaurant
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun CustomerHomeScreenContent(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val restaurants by viewModel.filteredRestaurants.collectAsState()
    val featuredRestaurants by viewModel.featuredRestaurants.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeOrders by viewModel.activeCustomerOrders.collectAsState()
    val latestActiveOrder = activeOrders.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_home_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input"),
                placeholder = {
                    Text(
                        text = if (isAr) "ابحث عن مطعم أو وجبة (شاورما، برجر، كشري...)" else "Search restaurants or dishes...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DrovaTextMuted, fontSize = 13.sp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.updateSearchQuery("") },
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "مسح",
                                tint = DrovaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DrovaTurquoise,
                    unfocusedBorderColor = DrovaBorder,
                    focusedContainerColor = DrovaSurface,
                    unfocusedContainerColor = DrovaSurface
                )
            )
        }

        // Active Order Quick Banner (if customer has an ongoing order)
        if (latestActiveOrder != null) {
            item {
                ActiveOrderBanner(
                    order = latestActiveOrder,
                    onClick = { viewModel.navigateToOrderTracking(latestActiveOrder.id) },
                    onAdvanceStep = { viewModel.advanceOrderSimulation(latestActiveOrder.id) }
                )
            }
        }

        // Promotional Hero Section
        item {
            PromotionalBanner(
                titleAr = "خصم 20% على طلبك الأول عبر DROVA",
                subtitleAr = "استخدم كود DROVA20 مع توصيل فائق السرعة",
                titleEn = "20% OFF Your First Order on DROVA",
                subtitleEn = "Use code DROVA20 with ultra-fast delivery"
            )
        }

        // Food Categories Horizontal Filter
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "التصنيفات" else "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) DrovaTurquoise else DrovaSurface,
                            border = BorderStroke(1.dp, if (isSelected) DrovaTurquoise else DrovaBorder),
                            modifier = Modifier
                                .clickable { viewModel.selectCategory(category) }
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else DrovaTextPrimary,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Featured Restaurants Carousel (when no active search)
        if (searchQuery.isBlank() && featuredRestaurants.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "المطاعم المميزة والأعلى تقييماً" else "Top-Rated Featured",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DrovaTurquoiseLight
                        ) {
                            Text(
                                text = if (isAr) "شركاء Pro" else "Pro Partners",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredRestaurants, key = { "feat_${it.id}" }) { rest ->
                            FeaturedRestaurantCard(
                                restaurant = rest,
                                onClick = { viewModel.navigateToRestaurant(rest.id) }
                            )
                        }
                    }
                }
            }
        }

        // Nearby & All Restaurants List Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "جميع المطاعم في منطقتك" else "All Restaurants in Your Area",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary
                    )
                )
                Text(
                    text = "${restaurants.size} ${if (isAr) "مطاعم متاحة" else "available"}",
                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                )
            }
        }

        // Restaurants List
        if (restaurants.isEmpty()) {
            item {
                EmptyRestaurantsView(
                    searchQuery = searchQuery,
                    onClearSearch = {
                        viewModel.updateSearchQuery("")
                        viewModel.selectCategory("الكل")
                    }
                )
            }
        } else {
            items(restaurants, key = { it.id }) { restaurant ->
                RestaurantListItem(
                    restaurant = restaurant,
                    onClick = { viewModel.navigateToRestaurant(restaurant.id) }
                )
            }
        }
    }
}

@Composable
private fun ActiveOrderBanner(
    order: Order,
    onClick: () -> Unit,
    onAdvanceStep: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("active_order_banner"),
        style = DrovaSurfaceStyle.HERO_DEEP,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DrovaCyanAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "طلبك قيد التنفيذ الآن" else "Active Live Order",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaCyanAccent,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = order.orderNumber,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.restaurantNameAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${order.items.size} ${if (isAr) "أصناف" else "items"} • ${order.totalEgp} ج.م",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaPrimary.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, DrovaPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = order.status.titleAr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaCyanAccent,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val currentIdx = order.status.stepIndex.coerceIn(1, 9)
            LinearProgressIndicator(
                progress = { currentIdx.toFloat() / 9f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = DrovaCyanAccent,
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "اضغط للتتبع المباشر ←" else "Tap for live tracking →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )

                // Quick simulation trigger button for instant CUJ review
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaCyanAccent,
                    modifier = Modifier
                        .clickable { onAdvanceStep() }
                        .testTag("banner_advance_step_btn")
                ) {
                    Text(
                        text = if (isAr) "محاكاة الخطوة التالية ⚡" else "Advance Step ⚡",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaDeep,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromotionalBanner(
    titleAr: String,
    subtitleAr: String,
    titleEn: String,
    subtitleEn: String
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(DrovaPrimary, DrovaDeepTeal)
                )
            )
            .padding(16.dp)
            .testTag("promo_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isAr) "عرض حصري" else "SPECIAL PROMO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAr) titleAr else titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isAr) subtitleAr else subtitleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturedRestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick)
            .testTag("featured_restaurant_${restaurant.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = if (isAr) "صورة ${restaurant.nameAr}" else restaurant.nameEn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .testTag("featured_restaurant_image_${restaurant.id}")
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaTurquoiseLight
                ) {
                    Text(
                        text = restaurant.categoryAr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoiseHover,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = DrovaWarning,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = restaurant.nameAr,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaTextPrimary,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = restaurant.addressAr,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DrovaTextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${restaurant.deliveryTimeMin} د",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = "${restaurant.deliveryFeeEgp} ج.م توصيل",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaPrimary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RestaurantListItem(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("restaurant_list_item_${restaurant.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!restaurant.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = restaurant.imageUrl,
                            contentDescription = if (isAr) "صورة ${restaurant.nameAr}" else restaurant.nameEn,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("restaurant_list_image_${restaurant.id}")
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DrovaSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = DrovaPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = restaurant.nameAr,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${restaurant.categoryAr} • ${restaurant.addressAr}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaTurquoiseLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DrovaWarning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${restaurant.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTurquoiseHover,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.deliveryTimeMin} ${if (isAr) "دقيقة" else "mins"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeliveryDining,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.deliveryFeeEgp} ${if (isAr) "ج.م توصيل" else "EGP delivery"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (isAr) "حد أدنى" else "Min"} ${restaurant.minOrderEgp.toInt()} ج.م",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRestaurantsView(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(DrovaSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = DrovaTextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isAr) "لا توجد نتائج مطابقة لـ \"$searchQuery\"" else "No restaurants found for \"$searchQuery\"",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DrovaTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isAr) "جرّب البحث باسم مطعم أو تصنيف آخر" else "Try searching for a different dish or category",
            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DrovaOutlinedButton(
            text = if (isAr) "عرض جميع المطاعم" else "View All Restaurants",
            onClick = onClearSearch,
            modifier = Modifier.width(200.dp),
            testTag = "empty_reset_search_btn"
        )
    }
}

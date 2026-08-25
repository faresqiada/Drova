package com.example.presentation.restaurant.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import coil.compose.AsyncImage
import com.example.core.designsystem.*
import com.example.domain.model.MenuItem
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.ui.theme.*

@Composable
fun RestaurantMenuTab(
    restaurantViewModel: RestaurantViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val categories by restaurantViewModel.menuCategories.collectAsState()
    val selectedCategory by restaurantViewModel.selectedMenuCategory.collectAsState()
    val filteredMenuItems by restaurantViewModel.filteredMenuItems.collectAsState()
    val searchQuery by restaurantViewModel.menuSearchQuery.collectAsState()
    val isAddEditOpen by restaurantViewModel.isAddEditProductOpen.collectAsState()
    val editingProduct by restaurantViewModel.editingProduct.collectAsState()

    // Add / Edit Dialog
    if (isAddEditOpen) {
        AddEditProductDialog(
            item = editingProduct,
            categories = categories,
            restaurantViewModel = restaurantViewModel,
            onDismiss = { restaurantViewModel.closeAddEditProductDialog() }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_menu_tab"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { restaurantViewModel.openAddProductDialog() },
                containerColor = DrovaTurquoise,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = if (isAr) "إضافة صنف جديد" else "Add Something",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("fab_add_menu_item")
            )
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Search & Category Filter
            Surface(
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { restaurantViewModel.updateMenuSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = if (isAr) "بحث في أصناف المنيو..." else "Search menu items...",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DrovaTurquoise
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { restaurantViewModel.updateMenuSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح",
                                        tint = DrovaTextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DrovaSurfaceVariant,
                            unfocusedContainerColor = DrovaSurfaceVariant,
                            focusedBorderColor = DrovaTurquoise,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("search_menu_items")
                    )

                    // Categories Horizontal Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) DrovaTurquoise else DrovaSurfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) DrovaTurquoise else DrovaBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { restaurantViewModel.selectMenuCategory(cat) }
                                    .testTag("menu_cat_$cat")
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

            // Products List
            if (filteredMenuItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = DrovaTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isAr) "لا توجد أصناف تطابق هذا البحث" else "No items found in this section",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAr) "اضغط على زر إضافة صنف جديد بالأسفل لإضافة أول صنف" else "Tap add button below to create a menu item",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMenuItems, key = { it.id }) { item ->
                        RestaurantMenuItemCard(
                            item = item,
                            onToggleAvailability = { isAvailable ->
                                restaurantViewModel.toggleMenuItemAvailability(item.id, isAvailable)
                            },
                            onEdit = { restaurantViewModel.openEditProductDialog(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestaurantMenuItemCard(
    item: MenuItem,
    onToggleAvailability: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onEdit() }
            .testTag("menu_item_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, if (item.isAvailable) DrovaBorder else DrovaError.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (!item.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = if (isAr) "صورة ${item.nameAr}" else item.nameEn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("menu_item_image_${item.id}")
                )
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = DrovaSurfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = if (isAr) "لا توجد صورة" else "No image",
                            tint = DrovaTextSecondary.copy(alpha = 0.65f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.nameAr,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isAvailable) DrovaTextPrimary else DrovaTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DrovaSurfaceVariant
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.nameEn.isNotBlank()) {
                        Text(
                            text = item.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (item.descriptionAr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.descriptionAr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 12.sp
                            ),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.price} ج.م",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaTurquoiseHover
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = DrovaTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.preparationTimeMin} دقيقة",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Availability Switch & Edit Action
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (item.isAvailable) (if (isAr) "متاح" else "Available")
                            else (if (isAr) "غير متوفر" else "Sold Out"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (item.isAvailable) DrovaSuccessText else DrovaErrorText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = item.isAvailable,
                            onCheckedChange = onToggleAvailability,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DrovaTurquoise,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = DrovaTextSecondary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("switch_avail_${item.id}")
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = DrovaTurquoise,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

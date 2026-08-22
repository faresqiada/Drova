package com.example.presentation.customer.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.domain.model.CartItem
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun CartScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val cartItems by viewModel.cartItems.collectAsState()
    val cartRestaurant by viewModel.cartRestaurant.collectAsState()
    val subtotal by viewModel.cartSubtotalEgp.collectAsState()
    val deliveryFee by viewModel.cartDeliveryFeeEgp.collectAsState()
    val total by viewModel.cartTotalEgp.collectAsState()
    val platformFee = viewModel.platformFeeEgp

    val minOrder = cartRestaurant?.minOrderEgp ?: 0.0
    val isBelowMinOrder = subtotal < minOrder && cartItems.isNotEmpty()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "سلة المشتريات" else "Shopping Basket",
                onBackClick = { viewModel.navigateBack() },
                trailingContent = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearCart() },
                            modifier = Modifier.testTag("cart_clear_all_btn")
                        ) {
                            Text(
                                text = if (isAr) "إفراغ" else "Clear",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DrovaError,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (isBelowMinOrder) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DrovaWarningContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = DrovaWarningText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr)
                                            "الحد الأدنى للطلب هو ${minOrder.toInt()} ج.م (أضف ${(minOrder - subtotal).toInt()} ج.م)"
                                        else
                                            "Min order is ${minOrder.toInt()} EGP (Add ${(minOrder - subtotal).toInt()} EGP)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaWarningText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        DrovaPrimaryButton(
                            text = if (isAr) "المتابعة لإتمام الطلب • ${total} ج.م" else "Proceed to Checkout • ${total} EGP",
                            onClick = { viewModel.navigateToCheckout() },
                            enabled = !isBelowMinOrder,
                            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                            testTag = "cart_proceed_to_checkout_btn"
                        )
                    }
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            EmptyCartView(
                onBrowseClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Restaurant Info Banner
                if (cartRestaurant != null) {
                    item {
                        DrovaSurface(
                            modifier = Modifier.fillMaxWidth(),
                            style = DrovaSurfaceStyle.TONAL,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = DrovaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = cartRestaurant!!.nameAr,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DrovaTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${cartRestaurant!!.deliveryTimeMin} ${if (isAr) "دقيقة توصيل تقريباً" else "mins delivery"}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DrovaTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DrovaSurface,
                                    border = BorderStroke(1.dp, DrovaBorder)
                                ) {
                                    Text(
                                        text = "${cartItems.size} ${if (isAr) "وجبات" else "items"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Cart Items List
                items(cartItems, key = { it.menuItem.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.updateCartItemQuantity(item.menuItem.id, item.quantity + 1) },
                        onDecrement = { viewModel.updateCartItemQuantity(item.menuItem.id, item.quantity - 1) },
                        onRemove = { viewModel.removeCartItem(item.menuItem.id) }
                    )
                }

                // Financial Breakdown Invoice Card
                item {
                    DrovaSectionContainer(
                        title = if (isAr) "تفاصيل الفاتورة" else "Order Summary",
                        style = DrovaSurfaceStyle.FLAT
                    ) {
                        InvoiceRow(
                            label = if (isAr) "مجموع الأصناف" else "Items Subtotal",
                            value = "${subtotal} ج.م"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InvoiceRow(
                            label = if (isAr) "رسوم التوصيل" else "Delivery Fee",
                            value = "${deliveryFee} ج.م"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InvoiceRow(
                            label = if (isAr) "رسوم الخدمة والتشغيل" else "Service Fee",
                            value = "${platformFee} ج.م"
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "المبلغ الإجمالي" else "Total Amount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaDeep,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "${total} ج.م",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaPrimary,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.menuItem.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.menuItem.nameAr,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary,
                        fontSize = 13.sp
                    )
                )

                if (item.specialNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ملاحظة: ${item.specialNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.totalEgp} ج.م (${item.menuItem.price} × ${item.quantity})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaPrimary,
                        fontSize = 12.sp
                    )
                )
            }

            // Stepper and delete
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                .testTag("cart_item_decrement_${item.menuItem.id}")
                        ) {
                            Icon(
                                imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                contentDescription = "تقليل",
                                tint = if (item.quantity == 1) DrovaError else DrovaPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "${item.quantity}",
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
                                .testTag("cart_item_increment_${item.menuItem.id}")
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
            }
        }
    }
}

@Composable
private fun InvoiceRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DrovaTextSecondary,
                fontSize = 12.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = DrovaTextPrimary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun EmptyCartView(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(DrovaSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = DrovaPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isAr) "سلة المشتريات فارغة" else "Your Basket is Empty",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = DrovaDeep,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isAr)
                "تصفح أفضل المطاعم وأضف وجباتك المفضلة لبدء تجربة التوصيل السريع مع DROVA"
            else
                "Explore partner restaurants and add your favorite meals to start your order",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DrovaTextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        DrovaPrimaryButton(
            text = if (isAr) "تصفح المطاعم الآن" else "Browse Restaurants",
            onClick = onBrowseClick,
            modifier = Modifier.width(220.dp),
            testTag = "empty_cart_browse_btn"
        )
    }
}

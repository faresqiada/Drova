package com.example.presentation.customer.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.domain.model.PaymentMethod
import com.example.domain.model.SavedAddress
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
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

    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val specialInstructions by viewModel.specialInstructions.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()
    val walletBalance by viewModel.customerWalletBalanceEgp.collectAsState()

    var showAddressSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("checkout_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "تأكيد الطلب والدفع" else "Checkout & Payment",
                onBackClick = { viewModel.navigateBack() }
            )
        },
        bottomBar = {
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
                    DrovaPrimaryButton(
                        text = if (isAr) "تأكيد وإرسال الطلب • ${total} ج.م" else "Place Order • ${total} EGP",
                        onClick = { viewModel.placeOrder() },
                        isLoading = isPlacingOrder,
                        enabled = cartItems.isNotEmpty() && !isPlacingOrder,
                        testTag = "checkout_confirm_order_btn"
                    )
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Delivery Address Card
            item {
                DrovaSectionContainer(
                    title = if (isAr) "عنوان التوصيل" else "Delivery Address",
                    trailingAction = {
                        TextButton(
                            onClick = { showAddressSheet = true },
                            modifier = Modifier.testTag("checkout_change_address_btn")
                        ) {
                            Text(
                                text = if (isAr) "تغيير" else "Change",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaPrimary
                                )
                            )
                        }
                    },
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DrovaTurquoiseLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DrovaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedAddress.labelAr,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedAddress.detailedAddressAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = selectedAddress.districtAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // 2. Special Instructions Input
            item {
                DrovaSectionContainer(
                    title = if (isAr) "ملاحظات وتوجيهات للكابتن" else "Delivery Instructions",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    OutlinedTextField(
                        value = specialInstructions,
                        onValueChange = { viewModel.updateSpecialInstructions(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_instructions_input"),
                        placeholder = {
                            Text(
                                text = if (isAr) "مثال: رن جرس الباب، التسليم عند البوابة، اتصل عند الوصول..." else "e.g. Ring the bell, deliver to 3rd floor...",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextMuted, fontSize = 12.sp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DrovaTurquoise,
                            unfocusedBorderColor = DrovaBorder,
                            focusedContainerColor = DrovaSurfaceVariant,
                            unfocusedContainerColor = DrovaSurfaceVariant
                        )
                    )
                }
            }

            // 3. Payment Method Selection
            item {
                DrovaSectionContainer(
                    title = if (isAr) "طريقة الدفع" else "Payment Method",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Option 1: Cash on Delivery (Functional)
                        PaymentMethodOption(
                            title = if (isAr) "الدفع نقداً عند الاستلام" else "Cash on Delivery",
                            subtitle = if (isAr) "ادفع للكابتن عند استلام وجبتك" else "Pay directly to the captain",
                            icon = Icons.Default.Payments,
                            isSelected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                            badge = if (isAr) "نشط ومتاح" else "Active",
                            badgeColor = DrovaSuccess,
                            onClick = { viewModel.selectPaymentMethod(PaymentMethod.CASH_ON_DELIVERY) },
                            testTag = "pay_method_cash"
                        )

                        // Option 2: DROVA Wallet (Ready Architecture)
                        PaymentMethodOption(
                            title = if (isAr) "محفظة DROVA" else "DROVA Wallet",
                            subtitle = "${if (isAr) "الرصيد المتاح:" else "Available balance:"} ${walletBalance} ج.م",
                            icon = Icons.Default.AccountBalanceWallet,
                            isSelected = selectedPaymentMethod == PaymentMethod.WALLET,
                            badge = if (walletBalance >= total) (if (isAr) "رصيد كافي" else "Ready") else (if (isAr) "رصيد غير كافي" else "Insufficient"),
                            badgeColor = if (walletBalance >= total) DrovaTurquoiseHover else DrovaWarning,
                            onClick = { viewModel.selectPaymentMethod(PaymentMethod.WALLET) },
                            testTag = "pay_method_wallet"
                        )

                        // Option 3: Credit/Debit Card (Ready Architecture)
                        PaymentMethodOption(
                            title = if (isAr) "بطاقة بنكية (فيزا / ماستركارد)" else "Credit / Debit Card",
                            subtitle = "•••• •••• •••• 4242 (مجهزة للتكامل)",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedPaymentMethod == PaymentMethod.CREDIT_CARD,
                            badge = if (isAr) "بوابة دفع آمنة" else "Secure Gateway",
                            badgeColor = DrovaPrimary,
                            onClick = { viewModel.selectPaymentMethod(PaymentMethod.CREDIT_CARD) },
                            testTag = "pay_method_card"
                        )
                    }
                }
            }

            // 4. Order Summary Overview
            item {
                DrovaSectionContainer(
                    title = if (isAr) "ملخص الأصناف (${cartItems.size})" else "Items Summary (${cartItems.size})",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.quantity} × ${item.menuItem.nameAr}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "${item.totalEgp} ج.م",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. Final Invoice Breakdown
            item {
                DrovaSectionContainer(
                    title = if (isAr) "الحساب النهائي" else "Final Bill Breakdown",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    CheckoutInvoiceRow(
                        label = if (isAr) "مجموع الوجبات" else "Subtotal",
                        value = "${subtotal} ج.م"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CheckoutInvoiceRow(
                        label = if (isAr) "رسوم التوصيل (${cartRestaurant?.nameAr})" else "Delivery Fee",
                        value = "${deliveryFee} ج.م"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CheckoutInvoiceRow(
                        label = if (isAr) "رسوم تشغيل المنصة" else "Platform Fee",
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
                            text = if (isAr) "الإجمالي المستحق" else "Total Payable",
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
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // Change Address Bottom Sheet
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            containerColor = DrovaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = if (isAr) "اختر عنوان التوصيل" else "Select Delivery Address",
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
                                viewModel.selectAddress(addr)
                                showAddressSheet = false
                            }
                            .testTag("address_option_${addr.id}")
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
                    onClick = { showAddressSheet = false }
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) DrovaTurquoiseLight else DrovaSurfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) DrovaPrimary else DrovaBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) DrovaPrimary else DrovaTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) DrovaPrimary else DrovaTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CheckoutInvoiceRow(
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

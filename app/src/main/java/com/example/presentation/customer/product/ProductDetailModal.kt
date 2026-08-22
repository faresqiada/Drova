package com.example.presentation.customer.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
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
import com.example.domain.model.MenuItem
import com.example.domain.model.Restaurant
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailModal(
    item: MenuItem,
    restaurant: Restaurant,
    onDismiss: () -> Unit,
    onAddToCart: (quantity: Int, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    var quantity by remember { mutableStateOf(1) }
    var specialNotes by remember { mutableStateOf("") }
    val totalPrice = remember(quantity, item.price) { quantity * item.price }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DrovaSurface,
        modifier = modifier.testTag("product_detail_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            // Header Row
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
                        text = "${restaurant.nameAr} • ${item.category}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoiseHover,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_product_modal_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = DrovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Item Title & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nameAr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaDeep,
                            fontSize = 17.sp
                        )
                    )
                    if (item.nameEn.isNotBlank()) {
                        Text(
                            text = item.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Text(
                    text = "${item.price} ج.م",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = DrovaPrimary,
                        fontSize = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            if (item.descriptionAr.isNotBlank()) {
                Text(
                    text = item.descriptionAr,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DrovaTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Special Notes Field
            Text(
                text = if (isAr) "ملاحظات خاصة للطلب (اختياري)" else "Special Instructions (Optional)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaTextPrimary,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = specialNotes,
                onValueChange = { specialNotes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_special_notes_input"),
                placeholder = {
                    Text(
                        text = if (isAr) "مثال: بدون بصل، صوص خارجي، تسوية زيادة..." else "e.g. No onions, sauce on the side...",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextMuted, fontSize = 12.sp)
                    )
                },
                maxLines = 2,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DrovaTurquoise,
                    unfocusedBorderColor = DrovaBorder,
                    focusedContainerColor = DrovaSurfaceVariant,
                    unfocusedContainerColor = DrovaSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quantity & Add to Cart Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stepper
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DrovaSurfaceVariant,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1 && item.isAvailable,
                            modifier = Modifier.size(36.dp).testTag("product_modal_decrement_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "تقليل",
                                tint = if (quantity > 1) DrovaPrimary else DrovaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary,
                                fontSize = 15.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { quantity++ },
                            enabled = item.isAvailable,
                            modifier = Modifier.size(36.dp).testTag("product_modal_increment_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "زيادة",
                                tint = DrovaPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Add to Cart Button
                DrovaPrimaryButton(
                    text = if (item.isAvailable)
                        "${if (isAr) "إضافة للسلة" else "Add to Basket"} • ${totalPrice} ج.م"
                    else
                        if (isAr) "غير متوفر حالياً" else "Currently Unavailable",
                    onClick = {
                        if (item.isAvailable) {
                            onAddToCart(quantity, specialNotes)
                        }
                    },
                    enabled = item.isAvailable,
                    modifier = Modifier.weight(1f),
                    testTag = "product_modal_add_to_cart_btn"
                )
            }
        }
    }
}

package com.example.presentation.restaurant.menu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import coil.compose.AsyncImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.designsystem.*
import com.example.domain.model.MenuItem
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.ui.theme.*

@Composable
fun AddEditProductDialog(
    item: MenuItem?,
    categories: List<String>,
    restaurantViewModel: RestaurantViewModel,
    onDismiss: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val isEditing = item != null

    var nameAr by remember { mutableStateOf(item?.nameAr.orEmpty()) }
    var nameEn by remember { mutableStateOf(item?.nameEn.orEmpty()) }
    var selectedCategory by remember { mutableStateOf(item?.category ?: (categories.filter { it != "الكل" }.firstOrNull() ?: "شاورما")) }
    var priceText by remember { mutableStateOf(if (item != null) "${item.price}" else "") }
    var descriptionAr by remember { mutableStateOf(item?.descriptionAr.orEmpty()) }
    var prepTimeText by remember { mutableStateOf(if (item != null) "${item.preparationTimeMin}" else "15") }
    var imageUri by remember { mutableStateOf(item?.imageUri) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri?.toString()
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("add_edit_product_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DrovaBackground,
            border = BorderStroke(1.dp, DrovaBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Surface(
                    color = DrovaSurface,
                    border = BorderStroke(0.dp, Color.Transparent),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isEditing) (if (isAr) "تعديل صنف في المنيو" else "Edit Menu Item")
                            else (if (isAr) "إضافة صنف جديد للمنيو" else "Add New Menu Item"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = DrovaTextPrimary
                            )
                        }
                    }
                }

                // Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DrovaErrorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaErrorText,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Arabic Name
                    Column {
                        Text(
                            text = if (isAr) "اسم الصنف بالعربية *" else "Item Name (Arabic) *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = nameAr,
                            onValueChange = {
                                nameAr = it
                                errorMessage = null
                            },
                            placeholder = { Text(if (isAr) "مثال: ساندوتش شاورما فراخ عربي" else "Arabic name") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DrovaSurface,
                                unfocusedContainerColor = DrovaSurface,
                                focusedBorderColor = DrovaTurquoise,
                                unfocusedBorderColor = DrovaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_product_name_ar")
                        )
                    }

                    // English Name
                    Column {
                        Text(
                            text = if (isAr) "اسم الصنف بالإنجليزية" else "Item Name (English)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = nameEn,
                            onValueChange = { nameEn = it },
                            placeholder = { Text(if (isAr) "مثال: Arabic Chicken Shawarma" else "English name") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DrovaSurface,
                                unfocusedContainerColor = DrovaSurface,
                                focusedBorderColor = DrovaTurquoise,
                                unfocusedBorderColor = DrovaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_product_name_en")
                        )
                    }

                    // Category Selector
                    Column {
                        Text(
                            text = if (isAr) "التصنيف / القسم" else "Category / Section",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val availableCats = (categories.filter { it != "الكل" } + listOf("شاورما", "وجبات", "مقبلات", "سندوتشات", "مشروبات", "حلويات")).distinct()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCats.take(4).forEach { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DrovaTurquoise,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Price in EGP & Prep Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "السعر (ج.م) *" else "Price (EGP) *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = {
                                    priceText = it
                                    errorMessage = null
                                },
                                placeholder = { Text("145.00") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DrovaSurface,
                                    unfocusedContainerColor = DrovaSurface,
                                    focusedBorderColor = DrovaTurquoise,
                                    unfocusedBorderColor = DrovaBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_product_price")
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "وقت التحضير (دقيقة)" else "Prep Time (min)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = prepTimeText,
                                onValueChange = { prepTimeText = it },
                                placeholder = { Text("15") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DrovaSurface,
                                    unfocusedContainerColor = DrovaSurface,
                                    focusedBorderColor = DrovaTurquoise,
                                    unfocusedBorderColor = DrovaBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_product_preptime")
                            )
                        }
                    }

                    // Product image
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isAr) "صورة المنتج" else "Product image",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        if (!imageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = if (isAr) "صورة المنتج" else "Product image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .testTag("product_image_preview")
                            )
                        }
                        OutlinedButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("button_pick_product_image"),
                            border = BorderStroke(1.dp, DrovaBorder)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isAr) "إضافة صورة من الهاتف" else "Add image from phone")
                        }
                        Text(
                            text = if (isAr) "سيتم حفظ الصورة المختارة مع المنتج داخل جلسة التطبيق الحالية." else "The selected image is saved with this item for the current app session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DrovaTextSecondary
                        )
                    }

                    // Description
                    Column {
                        Text(
                            text = if (isAr) "وصف الصنف والمكونات" else "Description & Ingredients",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = descriptionAr,
                            onValueChange = { descriptionAr = it },
                            placeholder = { Text(if (isAr) "المكونات، طريقة الطهي، الإضافات المجانية..." else "Description...") },
                            minLines = 3,
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DrovaSurface,
                                unfocusedContainerColor = DrovaSurface,
                                focusedBorderColor = DrovaTurquoise,
                                unfocusedBorderColor = DrovaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_product_desc")
                        )
                    }
                }

                // Bottom Action Buttons
                Surface(
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (isEditing) {
                                Button(
                                    onClick = {
                                        restaurantViewModel.deleteMenuItem(item!!.id)
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DrovaErrorContainer,
                                        contentColor = DrovaErrorText
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "حذف" else "Delete",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val price = priceText.toDoubleOrNull()
                                    val prepTime = prepTimeText.toIntOrNull() ?: 15

                                    if (nameAr.isBlank()) {
                                        errorMessage = if (isAr) "يرجى كتابة اسم الصنف بالعربية" else "Please enter product name"
                                        return@Button
                                    }
                                    if (price == null || price <= 0.0) {
                                        errorMessage = if (isAr) "يرجى إدخال سعر صحيح بالجنيه المصري" else "Please enter a valid price in EGP"
                                        return@Button
                                    }

                                    restaurantViewModel.saveProduct(
                                        nameAr = nameAr.trim(),
                                        nameEn = nameEn.trim(),
                                        category = selectedCategory,
                                        price = price,
                                        descriptionAr = descriptionAr.trim(),
                                        prepTimeMin = prepTime,
                                        imageUri = imageUri
                                    )
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEditing) (if (isAr) "حفظ التعديلات" else "Save Changes")
                                    else (if (isAr) "إضافة الصنف للمنيو" else "Add Item to Menu"),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

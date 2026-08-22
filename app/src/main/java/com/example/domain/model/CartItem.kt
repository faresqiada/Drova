package com.example.domain.model

data class CartItem(
    val menuItem: MenuItem,
    val restaurantId: String,
    val restaurantNameAr: String,
    val quantity: Int = 1,
    val specialNotes: String = ""
) {
    val totalEgp: Double get() = menuItem.price * quantity
}

data class SavedAddress(
    val id: String,
    val labelAr: String,
    val labelEn: String,
    val districtAr: String,
    val detailedAddressAr: String,
    val isDefault: Boolean = false
)

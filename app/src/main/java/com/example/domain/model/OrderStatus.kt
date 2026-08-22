package com.example.domain.model

/**
 * Shared canonical Order Lifecycle States across DROVA ecosystem:
 * - 9-stage active forward pipeline (CREATED -> COMPLETED)
 * - 2 terminal failure/exception states (CANCELLED, REJECTED)
 */
enum class OrderStatus(
    val stepIndex: Int,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val badgeLabelAr: String = titleAr,
    val badgeLabelEn: String = titleEn,
    val isTerminal: Boolean = false,
    val isFailure: Boolean = false
) {
    // Stage 1: Placed by Customer
    CREATED(
        stepIndex = 1,
        titleAr = "تم إنشاء الطلب",
        titleEn = "Order Created",
        descriptionAr = "في انتظار تأكيد المطعم والبدء",
        descriptionEn = "Waiting for restaurant confirmation",
        badgeLabelAr = "طلب جديد",
        badgeLabelEn = "New Order"
    ),

    // Stage 2: Confirmed by Restaurant
    RESTAURANT_CONFIRMED(
        stepIndex = 2,
        titleAr = "تم تأكيد المطعم",
        titleEn = "Restaurant Confirmed",
        descriptionAr = "المطعم وافق على الطلب وسيبدأ التحضير",
        descriptionEn = "Restaurant accepted the order",
        badgeLabelAr = "مؤكد",
        badgeLabelEn = "Confirmed"
    ),

    // Stage 3: In kitchen prep
    PREPARING(
        stepIndex = 3,
        titleAr = "جاري التحضير",
        titleEn = "Preparing",
        descriptionAr = "المطعم يقوم بإعداد وتجهيز الوجبات الآن",
        descriptionEn = "Kitchen is actively preparing the meal",
        badgeLabelAr = "في المطبخ",
        badgeLabelEn = "Cooking"
    ),

    // Stage 4: Packaged & ready for captain pickup
    READY_FOR_PICKUP(
        stepIndex = 4,
        titleAr = "جاهز للاستلام",
        titleEn = "Ready for Pickup",
        descriptionAr = "الطلب معبأ وجاهز وبانتظار استلام الكابتن",
        descriptionEn = "Order is packaged and waiting for captain",
        badgeLabelAr = "جاهز للتسليم",
        badgeLabelEn = "Ready"
    ),

    // Stage 5: Captain accepted & en-route to restaurant
    CAPTAIN_ASSIGNED(
        stepIndex = 5,
        titleAr = "تم تعيين الكابتن",
        titleEn = "Captain Assigned",
        descriptionAr = "الكابتن معين وفي طريقه لاستلام الطلب من المطعم",
        descriptionEn = "Captain is en route to pick up from restaurant",
        badgeLabelAr = "كابتن معين",
        badgeLabelEn = "Assigned"
    ),

    // Stage 6: Captain picked up from restaurant
    PICKED_UP(
        stepIndex = 6,
        titleAr = "تم الاستلام من المطعم",
        titleEn = "Picked Up",
        descriptionAr = "الكابتن استلم الطلب بنجاح من المطعم",
        descriptionEn = "Captain collected order from restaurant",
        badgeLabelAr = "تم الاستلام",
        badgeLabelEn = "Picked Up"
    ),

    // Stage 7: Captain traveling to delivery address
    ON_THE_WAY(
        stepIndex = 7,
        titleAr = "في الطريق إليك",
        titleEn = "On the Way",
        descriptionAr = "الكابتن يتجه إلى عنوان التوصيل المحدد",
        descriptionEn = "Captain is on the way to delivery address",
        badgeLabelAr = "في الطريق",
        badgeLabelEn = "On the Way"
    ),

    // Stage 8: Arrived / Handed to customer
    DELIVERED(
        stepIndex = 8,
        titleAr = "تم التوصيل",
        titleEn = "Delivered",
        descriptionAr = "تم تسليم الطلب بنجاح إلى العميل",
        descriptionEn = "Order handed over to customer",
        badgeLabelAr = "تم التوصيل",
        badgeLabelEn = "Delivered"
    ),

    // Stage 9: Final success & billing closure
    COMPLETED(
        stepIndex = 9,
        titleAr = "مكتمل",
        titleEn = "Completed",
        descriptionAr = "تم إنهاء الطلب بنجاح وإغلاق الحساب",
        descriptionEn = "Order successfully closed and settled",
        badgeLabelAr = "مكتمل",
        badgeLabelEn = "Completed",
        isTerminal = true
    ),

    // Exception: Cancelled
    CANCELLED(
        stepIndex = -1,
        titleAr = "ملغي",
        titleEn = "Cancelled",
        descriptionAr = "تم إلغاء الطلب من العميل أو خدمة العملاء",
        descriptionEn = "Order was cancelled",
        badgeLabelAr = "ملغي",
        badgeLabelEn = "Cancelled",
        isTerminal = true,
        isFailure = true
    ),

    // Exception: Rejected by restaurant
    REJECTED(
        stepIndex = -1,
        titleAr = "مرفوض",
        titleEn = "Rejected",
        descriptionAr = "تم رفض الطلب من المطعم لعدم التوفر أو ضغط العمل",
        descriptionEn = "Restaurant declined this order",
        badgeLabelAr = "مرفوض",
        badgeLabelEn = "Declined",
        isTerminal = true,
        isFailure = true
    );

    /**
     * Checks if this status is currently part of the active order lifecycle
     */
    val isActive: Boolean get() = !isTerminal

    /**
     * Returns the next sequential status in the standard 9-stage pipeline, or null if terminal
     */
    val nextStep: OrderStatus?
        get() = when (this) {
            CREATED -> RESTAURANT_CONFIRMED
            RESTAURANT_CONFIRMED -> PREPARING
            PREPARING -> READY_FOR_PICKUP
            READY_FOR_PICKUP -> CAPTAIN_ASSIGNED
            CAPTAIN_ASSIGNED -> PICKED_UP
            PICKED_UP -> ON_THE_WAY
            ON_THE_WAY -> DELIVERED
            DELIVERED -> COMPLETED
            COMPLETED, CANCELLED, REJECTED -> null
        }

    companion object {
        /**
         * The standard 9-stage active order pipeline in strict chronological order
         */
        val activePipeline: List<OrderStatus> = listOf(
            CREATED,
            RESTAURANT_CONFIRMED,
            PREPARING,
            READY_FOR_PICKUP,
            CAPTAIN_ASSIGNED,
            PICKED_UP,
            ON_THE_WAY,
            DELIVERED,
            COMPLETED
        )

        fun fromStringOrDefault(name: String?, default: OrderStatus = CREATED): OrderStatus {
            if (name.isNullOrBlank()) return default
            return try {
                valueOf(name.uppercase().trim())
            } catch (e: IllegalArgumentException) {
                default
            }
        }
    }

    val activePipelineSteps: List<OrderStatus> get() = activePipeline
}


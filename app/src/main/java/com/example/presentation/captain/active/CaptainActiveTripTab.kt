package com.example.presentation.captain.active

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.DeliveryTask
import com.example.domain.model.OrderStatus
import com.example.domain.model.PaymentMethod
import com.example.presentation.captain.CaptainMainTab
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.*
import java.io.File

@Composable
fun CaptainActiveTripTab(
    captainViewModel: CaptainViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val activeTask by captainViewModel.activeTask.collectAsState()
    val pickupProofState by captainViewModel.pickupProofState.collectAsState()
    val context = LocalContext.current
    var showCallDialog by remember { mutableStateOf<String?>(null) }
    var showDeliverySuccessDialog by remember { mutableStateOf(false) }
    var pendingCaptureFile by remember { mutableStateOf<File?>(null) }
    var pendingOrderId by remember { mutableStateOf<String?>(null) }

    fun createCaptureUri(): Uri {
        val directory = File(context.cacheDir, "pickup_proof").apply { mkdirs() }
        val file = File.createTempFile("pickup_proof_", ".jpg", directory)
        pendingCaptureFile = file
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { captured ->
        val file = pendingCaptureFile
        pendingCaptureFile = null
        val orderId = pendingOrderId
        pendingOrderId = null
        if (captured && file != null && orderId != null && file.exists()) {
            captainViewModel.confirmPickupWithProof(orderId, file)
        } else {
            file?.delete()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            runCatching { cameraLauncher.launch(createCaptureUri()) }
                .onFailure { error ->
                    pendingOrderId = null
                    pendingCaptureFile?.delete()
                    pendingCaptureFile = null
                    captainViewModel.setPickupProofFailure("تعذر تشغيل الكاميرا. حاول مرة أخرى.")
                }
        } else {
            pendingOrderId = null
            captainViewModel.setPickupProofFailure("يلزم السماح بالكاميرا لتصوير إثبات الاستلام.")
        }
    }

    fun openCamera(orderId: String) {
        captainViewModel.clearPickupProofState()
        pendingOrderId = orderId
        runCatching {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(createCaptureUri())
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }.onFailure {
            pendingOrderId = null
            pendingCaptureFile?.delete()
            pendingCaptureFile = null
            captainViewModel.setPickupProofFailure("تعذر تجهيز الكاميرا. حاول مرة أخرى.")
        }
    }

    if (activeTask == null) {
        // Empty State: No active trip currently
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("captain_no_active_trip_container")
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DrovaTurquoiseLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = DrovaTurquoise,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isAr) "لا توجد رحلة جارية حالياً" else "No Active Trip in Progress",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) "يمكنك قبول طلب جديد من الشاشة الرئيسية لبدء رحلة التوصيل" else "Accept an incoming request from the Home tab to start delivering",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DrovaTextSecondary
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { captainViewModel.selectTab(CaptainMainTab.HOME) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                        modifier = Modifier.testTag("btn_go_to_home_from_active")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "عرض الطلبات المتاحة" else "View Available Orders",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    } else {
        val task = activeTask!!

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("captain_active_trip_tab")
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Card: Order ID, Status, Earning Badge
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaCharcoal
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "الرحلة الجارية" else "Active Delivery",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                                )
                                Text(
                                    text = task.orderNumber,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }

                            DrovaStatusBadge(status = task.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "عائد الرحلة الصافي:" else "Net Trip Earning:",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                            )
                            Text(
                                text = "+${task.estimatedEarningsEgp} ج.م",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaCyanAccent
                                )
                            )
                        }
                    }
                }
            }

            // 2. Lifecycle Stepper Indicator
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "مراحل مسار التوصيل" else "Delivery Pipeline",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val currentStatus = task.status
                        val stages = listOf(
                            Triple(OrderStatus.CAPTAIN_ASSIGNED, "تعيين الكابتن", "Assigned"),
                            Triple(OrderStatus.PICKED_UP, "استلام المطعم", "Picked Up"),
                            Triple(OrderStatus.ON_THE_WAY, "في الطريق للعميل", "On the Way"),
                            Triple(OrderStatus.DELIVERED, "تم التسليم", "Delivered")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            stages.forEachIndexed { index, stage ->
                                val isPassed = when (currentStatus) {
                                    OrderStatus.CAPTAIN_ASSIGNED -> index == 0
                                    OrderStatus.PICKED_UP -> index <= 1
                                    OrderStatus.ON_THE_WAY -> index <= 2
                                    OrderStatus.DELIVERED, OrderStatus.COMPLETED -> true
                                    else -> false
                                }
                                val isCurrent = when (currentStatus) {
                                    OrderStatus.CAPTAIN_ASSIGNED -> index == 0
                                    OrderStatus.PICKED_UP -> index == 1
                                    OrderStatus.ON_THE_WAY -> index == 2
                                    OrderStatus.DELIVERED, OrderStatus.COMPLETED -> index == 3
                                    else -> false
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCurrent) DrovaTurquoise
                                                else if (isPassed) DrovaTurquoiseLight
                                                else DrovaSurfaceVariant
                                            )
                                            .then(
                                                if (isCurrent) Modifier
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPassed && !isCurrent) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = DrovaTurquoise,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCurrent) Color.White else DrovaTextSecondary
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAr) stage.second else stage.third,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) DrovaTurquoiseHover else DrovaTextSecondary
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Cash Collection Callout (if Cash on Delivery)
            if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captain_cash_collection_alert"),
                        shape = RoundedCornerShape(12.dp),
                        color = DrovaWarningContainer,
                        border = BorderStroke(1.5.dp, DrovaWarning)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = DrovaWarningText,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAr) "تحصيل نقدي عند الاستلام (كاش)" else "Cash to Collect",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaWarningText
                                    )
                                )
                                Text(
                                    text = if (isAr) "يرجى تحصيل ${task.orderTotalEgp} ج.م نقداً من العميل قبل إنهاء التسليم"
                                    else "Please collect ${task.orderTotalEgp} EGP in cash from customer",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaWarningText,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = DrovaSuccessContainer,
                        border = BorderStroke(1.dp, DrovaSuccess)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = DrovaSuccessText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "الطلب مدفوع إلكترونياً" else "Paid Electronically",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaSuccessText
                                    )
                                )
                                Text(
                                    text = if (isAr) "لا تقم بتحصيل أي مبالغ نقدية من العميل" else "Do not collect any cash from the customer",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaSuccessText)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Restaurant Pickup Point Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, if (task.status == OrderStatus.CAPTAIN_ASSIGNED) DrovaTurquoise else DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(DrovaCharcoal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "نقطة الاستلام (المطعم)" else "Pickup (Restaurant)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = task.restaurantNameAr,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                }
                            }

                            // Call Restaurant Button
                            IconButton(
                                onClick = { showCallDialog = task.restaurantPhone },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DrovaTurquoiseLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "اتصال بالمطعم",
                                    tint = DrovaTurquoiseHover,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "العنوان: ${task.restaurantAddressAr}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                        )
                        Text(
                            text = "المسافة المقدرة: ${task.pickupDistanceKm} كم",
                            style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                        )
                    }
                }
            }

            // 5. Customer Dropoff Point Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, if (task.status == OrderStatus.ON_THE_WAY) DrovaTurquoise else DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(DrovaTurquoise),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonPinCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "نقطة التسليم (العميل)" else "Dropoff (Customer)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = task.customerName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                }
                            }

                            // Call Customer Button
                            IconButton(
                                onClick = { showCallDialog = task.customerPhone },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DrovaTurquoiseLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "اتصال بالعميل",
                                    tint = DrovaTurquoiseHover,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "العنوان: ${task.customerAddressAr}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                        )
                        Text(
                            text = "مسافة التوصيل: ${task.dropoffDistanceKm} كم",
                            style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                        )

                        if (task.specialInstructions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DrovaSurfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = DrovaTurquoiseHover,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ملاحظات: ${task.specialInstructions}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaTextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Order Items Verification List Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) "محتويات الطلب للمطابقة" else "Order Items Verification",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (task.itemsList.isNotEmpty()) {
                            task.itemsList.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.nameAr}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DrovaTextPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "${item.totalEgp} ج.م",
                                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = task.itemsSummary,
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "إجمالي قيمة الطلب:" else "Order Total:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${task.orderTotalEgp} ج.م",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                        }
                    }
                }
            }

            // 7. Pickup Proof Validation Status
            if (pickupProofState !is com.example.presentation.captain.PickupProofUiState.Idle && task.status == OrderStatus.CAPTAIN_ASSIGNED) {
                item {
                    val statusText = when (val state = pickupProofState) {
                        com.example.presentation.captain.PickupProofUiState.Validating -> "جاري فحص الصورة ورفع الإثبات..."
                        com.example.presentation.captain.PickupProofUiState.Failure -> state.messageAr
                        com.example.presentation.captain.PickupProofUiState.Success -> "تم تأكيد استلام الطلب"
                        com.example.presentation.captain.PickupProofUiState.Idle -> ""
                    }
                    val isError = pickupProofState is com.example.presentation.captain.PickupProofUiState.Failure
                    Surface(
                        modifier = Modifier.fillMaxWidth().testTag("pickup_proof_validation_status"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isError) MaterialTheme.colorScheme.errorContainer else DrovaSuccessContainer,
                        border = BorderStroke(1.dp, if (isError) MaterialTheme.colorScheme.error else DrovaSuccess)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (pickupProofState is com.example.presentation.captain.PickupProofUiState.Validating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isError) MaterialTheme.colorScheme.error else DrovaSuccessText,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else DrovaSuccessText,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // 8. Contextual Lifecycle Action Buttons
            item {
                when (task.status) {
                    OrderStatus.CAPTAIN_ASSIGNED -> {
                        DrovaPrimaryButton(
                            text = if (isAr) "تصوير الفاتورة وتأكيد الاستلام" else "Photograph Invoice & Confirm Pickup",
                            onClick = { openCamera(task.orderId) },
                            leadingIcon = Icons.Default.CameraAlt,
                            testTag = "cap_confirm_pickup_btn"
                        )
                    }
                    OrderStatus.PICKED_UP -> {
                        DrovaPrimaryButton(
                            text = if (isAr) "بدء التحرك نحو العميل (في الطريق)" else "Start Heading to Customer",
                            onClick = {
                                captainViewModel.updateActiveTaskStatus(task.orderId, OrderStatus.ON_THE_WAY)
                            },
                            leadingIcon = Icons.Default.DirectionsBike,
                            testTag = "cap_on_the_way_btn"
                        )
                    }
                    OrderStatus.ON_THE_WAY -> {
                        DrovaPrimaryButton(
                            text = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY)
                                (if (isAr) "تأكيد التسليم وتحصيل ${task.orderTotalEgp} ج.م كاش" else "Confirm Delivered & Collected Cash")
                            else
                                (if (isAr) "تأكيد تسليم الطلب للعميل" else "Confirm Delivered to Customer"),
                            onClick = {
                                captainViewModel.updateActiveTaskStatus(task.orderId, OrderStatus.DELIVERED)
                            },
                            leadingIcon = Icons.Default.CheckCircle,
                            testTag = "cap_confirm_delivered_btn"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    // Call Simulation Dialog
    if (showCallDialog != null) {
        AlertDialog(
            onDismissRequest = { showCallDialog = null },
            title = {
                Text(
                    text = if (isAr) "الاتصال الهاتفي" else "Phone Call Simulation",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "جاري الاتصال بالرقم: ${showCallDialog!!}\n(ميزة تجريبية للبيئة الحية)"
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCallDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                ) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            },
            containerColor = DrovaSurface
        )
    }
}

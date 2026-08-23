package com.example.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.repository.AdminRecord
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaCharcoal
import com.example.ui.theme.DrovaBorder
import com.example.ui.theme.DrovaSurface
import com.example.ui.theme.DrovaTextSecondary
import com.example.ui.theme.DrovaTurquoise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val access by viewModel.accessState.collectAsState()
    val section by viewModel.selectedSection.collectAsState()
    val selectedRecord by viewModel.selectedRecord.collectAsState()
    val proofImageUrl by viewModel.proofImageUrl.collectAsState()
    val error by viewModel.lastError.collectAsState()
    val captains by viewModel.captains.collectAsState()
    val operationState by viewModel.operationState.collectAsState()

    when (access) {
        AdminAccessState.Loading -> CenterState("جارٍ التحقق من صلاحيات المدير…", "Verifying administrator access…", modifier)
        AdminAccessState.Denied -> AccessDeniedState(onLogout, modifier)
        AdminAccessState.Authorized -> Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = DrovaBackground,
            topBar = {
                SmallTopAppBar(
                    title = { Text("DROVA Admin", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DrovaTurquoise)
                    },
                    actions = {
                        IconButton(onClick = viewModel::verifyAndStart) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            }
        ) { padding ->
            if (selectedRecord != null) {
                AdminRecordDetail(
                    record = selectedRecord!!,
                    proofImageUrl = proofImageUrl,
                    eligibleCaptains = viewModel.eligibleCaptains(),
                    operationState = operationState,
                    onApproveAssignment = viewModel::approveAssignment,
                    onRejectAssignment = viewModel::rejectAssignment,
                    onClearOperationState = viewModel::clearOperationState,
                    onBack = viewModel::closeRecord,
                    modifier = Modifier.padding(padding)
                )
            } else {
                AdminHome(
                    section = section,
                    viewModel = viewModel,
                    error = error,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun AdminHome(
    section: AdminSection,
    viewModel: AdminViewModel,
    error: String?,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val restaurants by viewModel.restaurants.collectAsState()
    val captains by viewModel.captains.collectAsState()
    val users by viewModel.users.collectAsState()
    val assignments by viewModel.assignmentRequests.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminSection.values().forEach { item ->
                AssistChip(onClick = { viewModel.selectSection(item) }, label = { Text(item.titleAr) }, leadingIcon = null)
            }
        }
        if (error != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E8))) {
                Text("تعذر تحديث بيانات ${section.titleAr}. $error", modifier = Modifier.padding(12.dp), color = Color(0xFF9B1C1C))
            }
        }
        when (section) {
            AdminSection.OVERVIEW -> Overview(orders, restaurants, captains, users, assignments)
            AdminSection.ORDERS -> RecordList("الطلبات الحقيقية", orders, viewModel::openRecord)
            AdminSection.RESTAURANTS -> RecordList("المطاعم الحقيقية", restaurants, viewModel::openRecord)
            AdminSection.CAPTAINS -> RecordList("الكباتن الحقيقيون", captains, viewModel::openRecord)
            AdminSection.ASSIGNMENTS -> RecordList("طلبات التعيين", assignments, viewModel::openRecord)
            AdminSection.USERS -> RecordList("المستخدمون", users, viewModel::openRecord)
            AdminSection.FINANCE -> UnavailableSection("المالية", "لا يوجد Admin Finance contract حقيقي متاح في المشروع الحالي.")
            AdminSection.SETTINGS -> UnavailableSection("الإعدادات", "لا يوجد Settings contract حقيقي متاح في المشروع الحالي.")
        }
    }
}

@Composable
private fun Overview(
    orders: List<AdminRecord>,
    restaurants: List<AdminRecord>,
    captains: List<AdminRecord>,
    users: List<AdminRecord>,
    assignments: List<AdminRecord>
) {
    val active = orders.count { it.text("status") !in setOf("DELIVERED", "COMPLETED", "CANCELLED", "REJECTED") }
    val completed = orders.count { it.text("status") in setOf("DELIVERED", "COMPLETED") }
    val pendingAssignments = assignments.count { it.text("status")?.uppercase() == "PENDING" }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("ملخص البيانات الحقيقية", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item { MetricGrid(listOf("إجمالي الطلبات" to orders.size.toString(), "النشطة" to active.toString(), "المكتملة" to completed.toString(), "المطاعم" to restaurants.size.toString(), "الكباتن" to captains.size.toString(), "المستخدمون" to users.size.toString(), "طلبات التعيين المعلقة" to pendingAssignments.toString(), "المالية" to "N/A")) }
    }
}

@Composable
private fun MetricGrid(values: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (label, value) ->
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = DrovaSurface)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(label, color = DrovaTextSecondary, fontSize = 12.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(value, color = DrovaCharcoal, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RecordList(title: String, records: List<AdminRecord>, onOpen: (AdminRecord) -> Unit) {
    if (records.isEmpty()) {
        UnavailableSection(title, "لا توجد بيانات حقيقية متاحة حاليًا.")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(records, key = { it.id }) { record ->
            Card(onClick = { onOpen(record) }, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DrovaSurface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(record.text("name") ?: record.text("orderNumber") ?: record.id, fontWeight = FontWeight.Bold)
                    Text("ID: ${record.id}", color = DrovaTextSecondary, fontSize = 12.sp)
                    record.text("status")?.let { Text("Status: $it", color = DrovaTurquoise, fontSize = 13.sp) }
                    record.number("totalEgp")?.let { Text("Total: ${"%.2f".format(it)}", fontSize = 13.sp) }
                }
            }
        }
    }
}

@Composable
private fun AdminRecordDetail(
    record: AdminRecord,
    proofImageUrl: String?,
    eligibleCaptains: List<com.example.domain.repository.EligibleCaptain>,
    operationState: AdminOperationState,
    onApproveAssignment: (String, List<String>) -> Unit,
    onRejectAssignment: (String, String) -> Unit,
    onClearOperationState: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPendingAssignment = record.text("status")?.uppercase() == "PENDING" &&
        !record.text("restaurantId").isNullOrBlank() &&
        record.fields.containsKey("requestedCaptainCount")
    var selectedCaptainIds by remember(record.id) { mutableStateOf<Set<String>>(emptySet()) }
    var rejectionReason by remember(record.id) { mutableStateOf("") }
    var showApproveDialog by remember(record.id) { mutableStateOf(false) }
    var showRejectDialog by remember(record.id) { mutableStateOf(false) }
    var retryAction by remember(record.id) { mutableStateOf<(() -> Unit)?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("تفاصيل ${record.id}", fontWeight = FontWeight.Bold)
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            proofImageUrl?.let { url ->
                item {
                    AsyncImage(model = url, contentDescription = "Pickup Proof", modifier = Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop)
                }
            }
            if (isPendingAssignment) {
                item {
                    AssignmentControls(
                        eligibleCaptains = eligibleCaptains,
                        selectedCaptainIds = selectedCaptainIds,
                        operationState = operationState,
                        onToggleCaptain = { captainId ->
                            selectedCaptainIds = if (captainId in selectedCaptainIds) selectedCaptainIds - captainId else selectedCaptainIds + captainId
                        },
                        onApprove = { showApproveDialog = true },
                        onReject = { showRejectDialog = true },
                        onRetry = { retryAction?.invoke() },
                        onClearOperationState = onClearOperationState
                    )
                }
            }
            item { Text("البيانات الموثقة من Firestore", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(record.fields.entries.toList(), key = { it.key }) { (key, value) ->
                Surface(color = DrovaSurface, shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, DrovaBorder), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(key, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(value?.toString() ?: "N/A", color = DrovaTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("اعتماد طلب التعيين؟") },
            text = { Text("سيتم تنفيذ الاعتماد عبر Firestore transaction للكباتن المختارين فقط.") },
            confirmButton = {
                TextButton(onClick = {
                    showApproveDialog = false
                    retryAction = { onApproveAssignment(record.id, selectedCaptainIds.toList()) }
                    onApproveAssignment(record.id, selectedCaptainIds.toList())
                }) { Text("اعتماد") }
            },
            dismissButton = { TextButton(onClick = { showApproveDialog = false }) { Text("إلغاء") } }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("رفض طلب التعيين") },
            text = {
                OutlinedTextField(
                    value = rejectionReason,
                    onValueChange = { rejectionReason = it },
                    label = { Text("سبب الرفض") },
                    singleLine = false
                )
            },
            confirmButton = {
                TextButton(enabled = rejectionReason.isNotBlank(), onClick = {
                    showRejectDialog = false
                    retryAction = { onRejectAssignment(record.id, rejectionReason) }
                    onRejectAssignment(record.id, rejectionReason)
                }) { Text("رفض") }
            },
            dismissButton = { TextButton(onClick = { showRejectDialog = false }) { Text("إلغاء") } }
        )
    }
}

@Composable
private fun AssignmentControls(
    eligibleCaptains: List<com.example.domain.repository.EligibleCaptain>,
    selectedCaptainIds: Set<String>,
    operationState: AdminOperationState,
    onToggleCaptain: (String) -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRetry: () -> Unit,
    onClearOperationState: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = DrovaSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("الكباتن المؤهلون فقط", fontWeight = FontWeight.Bold)
            if (eligibleCaptains.isEmpty()) {
                Text("لا يوجد كابتن مؤهل حاليًا حسب approvalStatus/suspended/enabledForReceivingOrders.", color = DrovaTextSecondary)
            } else {
                eligibleCaptains.forEach { captain ->
                    AssistChip(
                        onClick = { onToggleCaptain(captain.id) },
                        label = { Text(if (captain.id in selectedCaptainIds) "✓ ${captain.name}" else captain.name) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = selectedCaptainIds.isNotEmpty() && operationState !is AdminOperationState.Loading, onClick = onApprove) { Text("اعتماد") }
                TextButton(enabled = operationState !is AdminOperationState.Loading, onClick = onReject) { Text("رفض") }
            }
            when (operationState) {
                AdminOperationState.Idle -> Unit
                AdminOperationState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DrovaTurquoise)
                is AdminOperationState.Success -> {
                    Text(operationState.message, color = DrovaTurquoise)
                    TextButton(onClick = onClearOperationState) { Text("إخفاء") }
                }
                is AdminOperationState.Failure -> {
                    Text(operationState.message, color = Color(0xFF9B1C1C))
                    TextButton(onClick = onRetry) { Text("إعادة المحاولة") }
                }
            }
        }
    }
}

@Composable
private fun UnavailableSection(title: String, message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = DrovaTextSecondary)
        }
    }
}

@Composable
private fun CenterState(ar: String, en: String, modifier: Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = DrovaTurquoise)
            Spacer(Modifier.height(12.dp))
            Text(ar)
        }
    }
}

@Composable
private fun AccessDeniedState(onLogout: () -> Unit, modifier: Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = DrovaTurquoise, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text("ليس لديك صلاحية Admin", fontWeight = FontWeight.Bold)
            Text("Admin custom claim is required.", color = DrovaTextSecondary)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLogout) { Text("تسجيل الخروج") }
        }
    }
}

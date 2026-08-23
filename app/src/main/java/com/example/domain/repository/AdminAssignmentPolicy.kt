package com.example.domain.repository

object AdminAssignmentPolicy {
    fun isEligible(approvalStatus: String?, suspended: Boolean, enabledForReceivingOrders: Boolean): Boolean =
        approvalStatus?.uppercase() == "APPROVED" && !suspended && enabledForReceivingOrders

    fun canApprove(status: String?, requestedCaptainCount: Int?, selectedCaptainIds: List<String>): Boolean =
        status?.uppercase() == "PENDING" &&
            requestedCaptainCount != null &&
            selectedCaptainIds.isNotEmpty() &&
            selectedCaptainIds.size == selectedCaptainIds.distinct().size &&
            selectedCaptainIds.size <= requestedCaptainCount

    fun stableAssignmentId(requestId: String, captainId: String): String =
        "${requestId.trim()}__${captainId.trim()}".replace(Regex("[^A-Za-z0-9_-]"), "_")
}

package com.example

import com.example.domain.repository.AdminAssignmentPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminAssignmentPolicyTest {
    @Test
    fun `only approved unsuspended enabled captain is eligible`() {
        assertTrue(AdminAssignmentPolicy.isEligible("APPROVED", suspended = false, enabledForReceivingOrders = true))
        assertFalse(AdminAssignmentPolicy.isEligible("PENDING", suspended = false, enabledForReceivingOrders = true))
        assertFalse(AdminAssignmentPolicy.isEligible("APPROVED", suspended = true, enabledForReceivingOrders = true))
        assertFalse(AdminAssignmentPolicy.isEligible("APPROVED", suspended = false, enabledForReceivingOrders = false))
    }

    @Test
    fun `approval requires pending request bounded unique selection`() {
        assertTrue(AdminAssignmentPolicy.canApprove("PENDING", 2, listOf("cap_1", "cap_2")))
        assertFalse(AdminAssignmentPolicy.canApprove("APPROVED", 2, listOf("cap_1")))
        assertFalse(AdminAssignmentPolicy.canApprove("PENDING", 1, listOf("cap_1", "cap_1")))
        assertFalse(AdminAssignmentPolicy.canApprove("PENDING", 1, emptyList()))
        assertFalse(AdminAssignmentPolicy.canApprove("PENDING", 1, listOf("cap_1", "cap_2")))
    }

    @Test
    fun `stable assignment id is deterministic and path safe`() {
        val first = AdminAssignmentPolicy.stableAssignmentId("request/1", "captain 2")
        val second = AdminAssignmentPolicy.stableAssignmentId("request/1", "captain 2")
        assertTrue(first == second)
        assertFalse(first.contains('/'))
        assertFalse(first.contains(' '))
    }
}

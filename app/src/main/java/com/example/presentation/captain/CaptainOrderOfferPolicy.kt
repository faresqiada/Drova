package com.example.presentation.captain

import com.example.domain.model.DeliveryTask

object CaptainOrderOfferPolicy {
    /**
     * Returns a compatible pending offer while preserving the active task.
     * No navigation, cancellation, or active-task mutation happens here.
     */
    fun findOrderInYourWay(
        activeTask: DeliveryTask?,
        availableTasks: List<DeliveryTask>
    ): DeliveryTask? {
        if (activeTask == null) return null
        return availableTasks.firstOrNull { it.orderId != activeTask.orderId }
    }
}

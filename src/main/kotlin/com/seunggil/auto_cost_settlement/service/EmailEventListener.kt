package com.seunggil.auto_cost_settlement.service


import com.seunggil.auto_cost_settlement.entity.EmailReceivedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EmailEventListener {

    @EventListener
    fun onEmailReceived(event: EmailReceivedEvent) {
        println("Received email: ${event.email}")
        // 추가 로직 구현
    }
}

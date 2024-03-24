package com.seunggil.auto_cost_settlement.controller.request

import java.util.Date

data class SettlementRequest(
        val id : String,
        val pw : String,
        val date: Date,
        val cost: Long
)

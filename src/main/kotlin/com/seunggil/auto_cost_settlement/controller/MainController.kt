package com.seunggil.auto_cost_settlement.controller

import com.mysql.cj.log.Log
import com.seunggil.auto_cost_settlement.controller.request.SettlementRequest
import com.seunggil.auto_cost_settlement.controller.request.UserRequest
import com.seunggil.auto_cost_settlement.database.repository.SettlementHistoryRepository
import com.seunggil.auto_cost_settlement.service.user.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MainController(
    private val userService: UserService,
    private val settlementHistoryRepository: SettlementHistoryRepository
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody userRequest: UserRequest): String {
        val user = userService.getUser(userRequest.id, userRequest.pw)

        return if (user != null) "success" else "fail"
    }

    @PostMapping("/settlements")
    fun getSettlementList(@RequestBody userRequest: UserRequest): ResponseEntity<Any> {
        val historyIndexList = userService.getUser(userRequest.id, userRequest.pw)?.let { user ->
            settlementHistoryRepository.findByUserAccount(user)
        }?.let { settlementHistoryList ->
            settlementHistoryList.map {
                it.historyIndex
            }
        } ?: listOf<Long>()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(historyIndexList)
    }

    @PostMapping("/settlements/search")
    fun getSettlementByDateAndCost(@RequestBody settlementRequest: SettlementRequest): ResponseEntity<Any> {
        val result = userService.getUser(settlementRequest.id, settlementRequest.pw)?.let {
            user -> settlementHistoryRepository.findByUserAndSettlement(
                userAccount = user, date= settlementRequest.date, cost = settlementRequest.cost)
        }
        return if(result.isNullOrEmpty()){
            ResponseEntity
                    .ok(ResponseEntity.noContent())

        }else{
            ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result[0].pdf)
        }
    }

    @GetMapping("/settlements/{historyIndex}")
    fun getPdf(@PathVariable historyIndex : Long) : ResponseEntity<Any>{
        val pdf = settlementHistoryRepository.findByHistoryIndex(historyIndex).pdf

        return if(pdf != null){
            ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf)
        }else{
            ResponseEntity
                .ok(ResponseEntity.EMPTY)
        }
    }
}

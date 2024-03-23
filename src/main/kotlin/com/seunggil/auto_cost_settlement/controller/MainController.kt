package com.seunggil.auto_cost_settlement.controller

import com.mysql.cj.log.Log
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

    @GetMapping("/register")
    fun registerUser(@RequestBody userRequest: UserRequest): String {
        val user = userService.getUser(userRequest.id, userRequest.pw)

        return if (user == null) "success" else "fail"
    }

    @GetMapping("/settlements")
    fun getSettlementList(@RequestBody userRequest: UserRequest): ResponseEntity<Any> {
        val historyIndexList = userService.getUser(userRequest.id, userRequest.pw)?.let { user ->
            settlementHistoryRepository.findByUser(user)
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
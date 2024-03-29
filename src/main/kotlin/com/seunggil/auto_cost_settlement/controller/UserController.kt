package com.seunggil.auto_cost_settlement.controller


import com.seunggil.auto_cost_settlement.controller.request.UserRequest
import com.seunggil.auto_cost_settlement.database.repository.KeystoreRepository
import com.seunggil.auto_cost_settlement.database.repository.SettlementHistoryRepository
import com.seunggil.auto_cost_settlement.service.settlement.EmailService
import com.seunggil.auto_cost_settlement.service.settlement.PdfService
import com.seunggil.auto_cost_settlement.service.user.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/users")
@RestController
class UserController(
    private val userService: UserService,
    private val emailService: EmailService,
    private val pdfService: PdfService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody userRequest: UserRequest): String {
        val user = userService.getUser(userRequest.id, userRequest.pw)
        if (user != null) {
            val results = emailService.readEmails(user.host, user.id, userRequest.pw)

            for (result in results) {
                pdfService.savePdfFromEmail(user, result)
            }
        }
        return if (user != null) "success" else "fail"
    }


    @PostMapping("/delete")
    fun deleteUser(@RequestBody userRequest: UserRequest): String {
        val result = userService.getUser(userRequest.id, userRequest.pw)?.let {
            userService.deleteUser(it)
        } ?: false

        return if (result) "success" else "fail"
    }
}

package com.seunggil.auto_cost_settlement.controller

import com.seunggil.auto_cost_settlement.service.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val userService: UserService
) {

    @GetMapping("/")
    fun helloWorld(): String {
        userService.getUser(id = "", password = "")
        return "Hello, World!"
    }
}

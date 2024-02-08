package com.seunggil.auto_cost_settlement.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    fun helloWorld(): String {
        return "Hello, World!"
    }
}

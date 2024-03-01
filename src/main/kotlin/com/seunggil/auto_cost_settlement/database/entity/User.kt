package com.seunggil.auto_cost_settlement.database.entity


import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userIndex : Long? = null,

    @Column(nullable = false)
    val host: String,

    @Column(nullable = false, unique = true)
    val id: String,

    @Column(nullable = false)
    val encryptedPassword: String
)
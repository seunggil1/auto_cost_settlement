package com.seunggil.auto_cost_settlement.database.entity

import jakarta.persistence.*


@Entity
class Keystore(
    @Id
    val userIndex: Long? = null,

    @Column(nullable = false)
    val passKey: String,

    @OneToOne
    @MapsId
    @JoinColumn(name = "userIndex")
    val userAccount: UserAccount
)

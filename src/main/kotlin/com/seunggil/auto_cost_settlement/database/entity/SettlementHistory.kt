package com.seunggil.auto_cost_settlement.database.entity

import jakarta.persistence.*
import java.util.Date

@Entity
class SettlementHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val historyIndex: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userIndex", referencedColumnName = "userIndex")
    val user: User,

    @Column(nullable = false)
    val cost: Long,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val date: Date,

    @Lob
    @Column
    val pdf: ByteArray? = null
)
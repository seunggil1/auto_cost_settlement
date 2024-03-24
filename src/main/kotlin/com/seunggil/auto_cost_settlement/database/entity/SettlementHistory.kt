package com.seunggil.auto_cost_settlement.database.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
class SettlementHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val historyIndex: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userIndex", referencedColumnName = "userIndex")
    val userAccount: UserAccount,

    @Column(nullable = false)
    val cost: Long,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val date: ZonedDateTime,

    @Lob
    @Column(columnDefinition = "BLOB")
    @Basic(fetch = FetchType.LAZY)
    val pdf: ByteArray? = null
)
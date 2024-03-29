package com.seunggil.auto_cost_settlement.database.repository

import com.seunggil.auto_cost_settlement.database.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserAccountRepository : JpaRepository<UserAccount, Long> {
    fun findById(id : String): UserAccount?

    fun deleteByUserIndex(userIndex: Long) : Unit
}

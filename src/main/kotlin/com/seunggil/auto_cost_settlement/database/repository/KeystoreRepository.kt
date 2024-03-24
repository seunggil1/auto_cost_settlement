package com.seunggil.auto_cost_settlement.database.repository

import com.seunggil.auto_cost_settlement.database.entity.Keystore
import com.seunggil.auto_cost_settlement.database.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface KeystoreRepository : JpaRepository<Keystore, Long>{
    fun findByUserAccount(userAccount: UserAccount) : Keystore?
    fun findByUserIndex(userIndex : Long) : Keystore?

}
package com.seunggil.auto_cost_settlement.database.repository

import com.seunggil.auto_cost_settlement.database.entity.Keystore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface KeystoreRepository : JpaRepository<Keystore, Long>{

}
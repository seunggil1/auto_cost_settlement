package com.seunggil.auto_cost_settlement.database.repository

import com.seunggil.auto_cost_settlement.database.entity.SettlementHistory
import com.seunggil.auto_cost_settlement.database.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date


@Repository
interface SettlementHistoryRepository : JpaRepository<SettlementHistory, Long>{
    fun findByUser(user: User): List<SettlementHistory>

    @Query("""SELECT sh FROM SettlementHistory sh 
    WHERE sh.user = :user
    AND FUNCTION('DATE_FORMAT', sh.date, '%Y-%m-%d %H:%i')
    = FUNCTION('DATE_FORMAT', :date, '%Y-%m-%d %H:%i')""")
    fun findByUserAndDate(user: User, date: Date): List<SettlementHistory>
}
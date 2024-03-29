package com.seunggil.auto_cost_settlement.database.repository

import com.seunggil.auto_cost_settlement.database.entity.SettlementHistory
import com.seunggil.auto_cost_settlement.database.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface SettlementHistoryRepository : JpaRepository<SettlementHistory, Long> {
    fun findByUserAccount(userAccount: UserAccount): List<SettlementHistory>

    @Query(
        """SELECT sh FROM SettlementHistory sh 
        WHERE sh.userAccount = :userAccount
        AND YEAR(sh.date) = YEAR(:date) AND MONTH(sh.date) = MONTH(:date)
        AND DAY(sh.date) = DAY(:date) AND HOUR(sh.date) = HOUR(:date)
        AND MINUTE(sh.date) = MINUTE(:date)
        AND sh.cost = :cost
    """
    )
    fun findByUserAndSettlement(userAccount: UserAccount, date: Date, cost: Long): List<SettlementHistory>


    fun findByHistoryIndex(historyIndex: Long): SettlementHistory

    fun deleteAllByUserAccount(userAccount: UserAccount): Unit
}
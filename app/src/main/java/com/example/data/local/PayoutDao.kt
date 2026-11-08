package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Payout
import kotlinx.coroutines.flow.Flow

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payouts WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getPayoutsByCreator(creatorId: String): Flow<List<Payout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: Payout)
}

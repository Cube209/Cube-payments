package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SecurityLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityLogDao {
    @Query("SELECT * FROM security_logs WHERE creatorId = :creatorId ORDER BY timestamp DESC")
    fun getLogsByCreator(creatorId: String): Flow<List<SecurityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SecurityLog)
}

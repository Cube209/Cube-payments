package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getTransactionsByCreator(creatorId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE cubeId = :cubeId ORDER BY createdAt DESC")
    fun getTransactionsByCube(cubeId: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
}

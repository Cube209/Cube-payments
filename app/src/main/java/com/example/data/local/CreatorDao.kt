package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Creator
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatorDao {
    @Query("SELECT * FROM creators WHERE id = :id")
    fun getCreatorById(id: String): Flow<Creator?>

    @Query("SELECT * FROM creators WHERE id = :id")
    suspend fun getCreatorByIdOnce(id: String): Creator?

    @Query("SELECT * FROM creators WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getCreatorByUsername(username: String): Creator?

    @Query("SELECT * FROM creators WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getCreatorByEmail(email: String): Creator?

    @Query("SELECT * FROM creators")
    fun getAllCreators(): Flow<List<Creator>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreator(creator: Creator)

    @Update
    suspend fun updateCreator(creator: Creator)

    @Query("UPDATE creators SET balance = balance + :amount WHERE id = :creatorId")
    suspend fun addBalance(creatorId: String, amount: Double)

    @Query("UPDATE creators SET balance = balance - :amount WHERE id = :creatorId")
    suspend fun deductBalance(creatorId: String, amount: Double)
}

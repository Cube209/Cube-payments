package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Cube
import kotlinx.coroutines.flow.Flow

@Dao
interface CubeDao {
    @Query("SELECT * FROM cubes WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getCubesByCreator(creatorId: String): Flow<List<Cube>>

    @Query("SELECT COUNT(*) FROM cubes WHERE creatorId = :creatorId")
    fun getCubeCountByCreator(creatorId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM cubes WHERE creatorId = :creatorId")
    suspend fun getCubeCountByCreatorOnce(creatorId: String): Int

    @Query("SELECT * FROM cubes WHERE id = :id LIMIT 1")
    fun getCubeById(id: String): Flow<Cube?>

    @Query("SELECT * FROM cubes WHERE id = :id LIMIT 1")
    suspend fun getCubeByIdOnce(id: String): Cube?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCube(cube: Cube)

    @Update
    suspend fun updateCube(cube: Cube)

    @Query("DELETE FROM cubes WHERE id = :id")
    suspend fun deleteCubeById(id: String)

    @Query("UPDATE cubes SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateCubeStatus(id: String, status: String, timestamp: Long = System.currentTimeMillis())
}

package com.example.data.dao

import androidx.room.*
import com.example.data.model.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM ai_memory")
    fun getAllMemoryFlow(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM ai_memory WHERE `key` = :key")
    suspend fun getMemoryValue(key: String): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("DELETE FROM ai_memory WHERE `key` = :key")
    suspend fun deleteMemory(key: String)
}

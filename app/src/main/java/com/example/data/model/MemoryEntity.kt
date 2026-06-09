package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_memory")
data class MemoryEntity(
    @PrimaryKey val key: String, // e.g. "user_name", "preferred_color_accent", "framework_preference", "auto_bug_fixes_count"
    val value: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

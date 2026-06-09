package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val prompt: String,
    val pagesJson: String, // JSON serialized List<WebPage>
    val primaryColor: String = "#00F0FF", // Jarvis Hologram Cyan
    val secondaryColor: String = "#7000FF", // Jarvis Purple
    val layoutStyle: String = "Futuristic",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class WebPage(
    val fileName: String, // e.g. "index.html", "about.html", "styles.css", "script.js"
    val content: String
)

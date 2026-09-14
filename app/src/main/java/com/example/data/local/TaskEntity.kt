package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General", // Work, Study, Personal, Quick
    val priority: String = "Medium",  // High, Medium, Low
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val source: String = "manual"     // "manual" or "ai_assistant"
)

package com.qinglong.panel.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val command: String,
    val schedule: String,
    val status: Int = 0,
    val lastRunTime: Long = 0,
    val nextRunTime: Long = 0,
    val logPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "environment_variables")
data class EnvironmentVariableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val value: String,
    val description: String = "",
    val isEncrypted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scripts")
data class ScriptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val path: String,
    val content: String = "",
    val dependencies: String = "",
    val version: String = "1.0.0",
    val author: String = "",
    val description: String = "",
    val isSystem: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int = 0,
    val taskName: String,
    val content: String,
    val level: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "update_history")
data class UpdateHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val version: String,
    val updateTime: Long = System.currentTimeMillis(),
    val status: Int = 0,
    val description: String = ""
)

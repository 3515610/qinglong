package com.qinglong.panel.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE status = :status")
    suspend fun getTasksByStatus(status: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, status: Int)

    @Query("UPDATE tasks SET lastRunTime = :lastRunTime, nextRunTime = :nextRunTime WHERE id = :taskId")
    suspend fun updateTaskRunTime(taskId: Int, lastRunTime: Long, nextRunTime: Long)
}

@Dao
interface EnvironmentVariableDao {

    @Query("SELECT * FROM environment_variables ORDER BY name ASC")
    fun getAllVariables(): Flow<List<EnvironmentVariableEntity>>

    @Query("SELECT * FROM environment_variables WHERE name = :name LIMIT 1")
    suspend fun getVariableByName(name: String): EnvironmentVariableEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariable(variable: EnvironmentVariableEntity): Long

    @Update
    suspend fun updateVariable(variable: EnvironmentVariableEntity)

    @Delete
    suspend fun deleteVariable(variable: EnvironmentVariableEntity)

    @Query("DELETE FROM environment_variables WHERE id = :id")
    suspend fun deleteVariableById(id: Int)
}

@Dao
interface ScriptDao {

    @Query("SELECT * FROM scripts ORDER BY name ASC")
    fun getAllScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE id = :scriptId")
    suspend fun getScriptById(scriptId: Int): ScriptEntity?

    @Query("SELECT * FROM scripts WHERE isSystem = :isSystem")
    suspend fun getScriptsByType(isSystem: Boolean): List<ScriptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: ScriptEntity): Long

    @Update
    suspend fun updateScript(script: ScriptEntity)

    @Delete
    suspend fun deleteScript(script: ScriptEntity)

    @Query("UPDATE scripts SET content = :content, updatedAt = :updatedAt WHERE id = :scriptId")
    suspend fun updateScriptContent(scriptId: Int, content: String, updatedAt: Long = System.currentTimeMillis())
}

@Dao
interface LogDao {

    @Query("SELECT * FROM logs WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogsByTaskId(taskId: Int): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int = 100): List<LogEntity>

    @Query("SELECT * FROM logs WHERE level = :level ORDER BY timestamp DESC")
    suspend fun getLogsByLevel(level: Int): List<LogEntity>

    @Insert
    suspend fun insertLog(log: LogEntity): Long

    @Insert
    suspend fun insertLogs(logs: List<LogEntity>): List<Long>

    @Query("DELETE FROM logs WHERE taskId = :taskId")
    suspend fun deleteLogsByTaskId(taskId: Int)

    @Query("DELETE FROM logs WHERE timestamp < :timestamp")
    suspend fun deleteOldLogs(timestamp: Long)
}

@Dao
interface UpdateHistoryDao {

    @Query("SELECT * FROM update_history ORDER BY updateTime DESC")
    fun getAllUpdates(): Flow<List<UpdateHistoryEntity>>

    @Query("SELECT * FROM update_history WHERE id = :id LIMIT 1")
    suspend fun getUpdateById(id: Int): UpdateHistoryEntity?

    @Query("SELECT * FROM update_history WHERE version = :version LIMIT 1")
    suspend fun getUpdateByVersion(version: String): UpdateHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: UpdateHistoryEntity): Long

    @Query("DELETE FROM update_history WHERE id = :id")
    suspend fun deleteUpdate(id: Int)
}

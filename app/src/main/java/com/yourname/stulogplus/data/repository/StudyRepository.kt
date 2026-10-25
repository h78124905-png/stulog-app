package com.yourname.stulogplus.data.repository

import com.yourname.stulogplus.data.local.*
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val db: AppDatabase) {
    fun subjects(): Flow<List<Subject>> = db.subjectDao().getAll()
    suspend fun addSubject(name: String, color: Int) = db.subjectDao().insert(Subject(name = name, color = color))
    fun tasks(date: String): Flow<List<Task>> = db.taskDao().forDate(date)
    fun allTasks(): Flow<List<Task>> = db.taskDao().all()
    suspend fun addTask(title: String, date: String) = db.taskDao().insert(Task(title = title, date = date))
    suspend fun updateTask(task: Task) = db.taskDao().update(task)
    suspend fun deleteTask(task: Task) = db.taskDao().delete(task)
    fun records(date: String): Flow<List<StudyRecord>> = db.studyRecordDao().forDate(date)
    fun allRecords(): Flow<List<StudyRecord>> = db.studyRecordDao().all()
    suspend fun addRecord(seconds: Int, date: String) = db.studyRecordDao().insert(StudyRecord(date = date, actualTimeSeconds = seconds))
    suspend fun clearAll() { db.clearAllTables() }
}

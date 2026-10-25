package com.yourname.stulogplus.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY orderIndex, id") fun getAll(): Flow<List<Subject>>
    @Insert suspend fun insert(item: Subject): Long
    @Delete suspend fun delete(item: Subject)
}
@Dao interface MaterialDao {
    @Query("SELECT * FROM materials WHERE subjectId = :subjectId ORDER BY id") fun forSubject(subjectId: Long): Flow<List<Material>>
    @Insert suspend fun insert(item: Material): Long
    @Delete suspend fun delete(item: Material)
}
@Dao interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC") fun forDate(date: String): Flow<List<Task>>
    @Query("SELECT * FROM tasks ORDER BY date DESC, id DESC") fun all(): Flow<List<Task>>
    @Insert suspend fun insert(item: Task): Long
    @Update suspend fun update(item: Task)
    @Delete suspend fun delete(item: Task)
}
@Dao interface StudyRecordDao {
    @Query("SELECT * FROM study_records WHERE date = :date ORDER BY createdAt DESC") fun forDate(date: String): Flow<List<StudyRecord>>
    @Query("SELECT * FROM study_records ORDER BY createdAt DESC") fun all(): Flow<List<StudyRecord>>
    @Insert suspend fun insert(item: StudyRecord): Long
    @Query("DELETE FROM study_records") suspend fun clear()
}

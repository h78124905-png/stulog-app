package com.yourname.stulogplus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Subject::class, Material::class, Task::class, StudyRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun materialDao(): MaterialDao
    abstract fun taskDao(): TaskDao
    abstract fun studyRecordDao(): StudyRecordDao
}

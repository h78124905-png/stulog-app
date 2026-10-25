package com.yourname.stulogplus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val color: Int, val orderIndex: Int = 0)

@Entity(tableName = "materials", foreignKeys = [ForeignKey(entity = Subject::class, parentColumns = ["id"], childColumns = ["subjectId"], onDelete = ForeignKey.CASCADE)])
data class Material(@PrimaryKey(autoGenerate = true) val id: Long = 0, val subjectId: Long, val name: String, val startPage: Int? = null, val endPage: Int? = null, val startDate: Long? = null, val endDate: Long? = null, val planMode: String = "deadline")

@Entity(tableName = "tasks")
data class Task(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val date: String, val isCompleted: Boolean = false, val timeSpentSeconds: Int = 0)

@Entity(tableName = "study_records", foreignKeys = [ForeignKey(entity = Material::class, parentColumns = ["id"], childColumns = ["materialId"], onDelete = ForeignKey.SET_NULL)])
data class StudyRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val materialId: Long? = null, val date: String, val plannedTimeSeconds: Int = 0, val actualTimeSeconds: Int = 0, val startPage: Int? = null, val endPage: Int? = null, val createdAt: Long = System.currentTimeMillis())

package com.yourname.stulogplus

import android.app.Application
import androidx.room.Room
import com.yourname.stulogplus.data.local.AppDatabase

class StulogPlusApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "stulogplus_database").build()
    }
}

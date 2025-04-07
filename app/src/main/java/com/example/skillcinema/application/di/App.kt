package com.example.skillcinema.application.di

import com.example.skillcinema.localdatabase.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: BaseApp(){
    companion object{
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.createInstance(applicationContext)
    }

}
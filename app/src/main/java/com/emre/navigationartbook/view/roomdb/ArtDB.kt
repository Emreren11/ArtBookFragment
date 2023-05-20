package com.emre.navigationartbook.view.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emre.navigationartbook.view.model.Arts

@Database(entities = [Arts::class], version = 1)
abstract class ArtDB : RoomDatabase() {
    abstract fun artDao(): ArtDao
}
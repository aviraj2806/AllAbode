package com.mp2.allabode.databse

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase

@Database(entities = [FlatEntity::class],version = 1)
abstract class FlatDatabase : RoomDatabase() {

    abstract fun flatDao(): FlatDao

}
package com.mp2.allabode.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RequestEntity::class],version = 1)
abstract class RequestDatabase: RoomDatabase() {

    abstract fun requestDao(): RequestDao
}
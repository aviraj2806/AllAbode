package com.mp2.allabode.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request")
data class RequestEntity (
    @PrimaryKey val timeStamp: String,
    @ColumnInfo(name = "student_mobile") val student: String,
    @ColumnInfo(name = "owner_mobile") val owner: String,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "flat_time_stamp") val flatTimeStamp:String
)
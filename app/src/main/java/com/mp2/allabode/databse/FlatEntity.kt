package com.mp2.allabode.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flat")
data class FlatEntity(
    @PrimaryKey val timeStamp: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "rent") val rent:String,
    @ColumnInfo(name = "full_add") val fullAdd:String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "owner_mobile") val ownerMobile: String
)
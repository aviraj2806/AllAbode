package com.mp2.allabode.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey val mobile: String,
    @ColumnInfo (name = "type") val type: String,
    @ColumnInfo (name = "name") val name: String,
    @ColumnInfo (name = "email") val email: String,
    @ColumnInfo (name = "image") val image: String,
    @ColumnInfo (name = "pass") val pass: String,
    @ColumnInfo (name = "student_age") val age: String,
    @ColumnInfo (name = "student_university") val uni: String
)
package com.mp2.allabode.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertUser(userEntity: UserEntity)

    @Query("SELECT * FROM user WHERE mobile =:mobile")
    fun getUserByMobile(mobile: String): UserEntity

    @Query("UPDATE user SET student_age =:age WHERE mobile =:mobile")
    fun updateStudentAge(age: String,mobile: String)

    @Query("UPDATE user SET student_university =:uni WHERE mobile =:mobile")
    fun updateStudentUniversity(uni: String,mobile: String)
}
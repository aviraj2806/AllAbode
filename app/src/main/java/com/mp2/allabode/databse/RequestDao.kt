package com.mp2.allabode.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RequestDao {

    @Insert
    fun insertRequest(requestEntity: RequestEntity)

    @Query("SELECT * FROM request WHERE owner_mobile =:mobile")
    fun getRequestByOwner(mobile: String): List<RequestEntity>

    @Query("SELECT * FROM request WHERE student_mobile =:mobile")
    fun getRequestByStudent(mobile: String): List<RequestEntity>

    @Query("UPDATE request SET status =:status WHERE timeStamp =:time")
    fun updateStatus(status:Int,time:String)
}
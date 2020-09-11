package com.mp2.allabode.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FlatDao {

    @Insert
    fun insertFlat(flatEntity: FlatEntity)

    @Query("SELECT * FROM flat WHERE city = :city")
    fun getFlatByCity(city: String): List<FlatEntity>

    @Query("SELECT * FROM flat WHERE owner_mobile =:mobile")
    fun getFlatByOwnerMobile(mobile: String): List<FlatEntity>

    @Query("SELECT * FROM flat")
    fun getAllFlat(): List<FlatEntity>

    @Query("SELECT DISTINCT city FROM flat")
    fun getAllCity(): List<String>

    @Query("SELECT * FROM flat where timeStamp =:time")
    fun getFlatByTime(time: String): FlatEntity
}
package com.khairy.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khairy.database.entities.ProfileEntity

/**
 *  Profile local database functions
 */
@Dao
interface ProfileDao {

    /***
     *  @param profileEntity [ProfileEntity]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profileEntity: ProfileEntity)

    /**
     *  @param userId Long
     *  @return [ProfileEntity]?
     */
    @Query("SELECT * FROM profile WHERE id = :userId")
    suspend fun getProfileById(userId: Long): ProfileEntity?
}
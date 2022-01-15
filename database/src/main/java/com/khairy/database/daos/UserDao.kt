package com.khairy.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khairy.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 *  User local database functions
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userCached: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usersCached: List<UserEntity>)

    @Query("SELECT * FROM user WHERE login = :userLogin")
    suspend fun getUserByLogin(userLogin: String): UserEntity

    @Query("SELECT * FROM user WHERE login LIKE '%' || :input || '%'")
    suspend fun searchByLogin(input: String): List<UserEntity>

    @Query("SELECT * FROM user ORDER BY id LIMIT :limit")
    fun queryAllUntil(limit: Int): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: Long): Flow<UserEntity>

    @Query("DELETE FROM user")
    suspend fun clearUsers()
}
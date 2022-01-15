package com.khairy.database.daos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khairy.database.entities.NotesEntity

/**
 *  Profile local database functions
 */
@Dao
interface NotesDao {

    /***
     *  @param notesCached [NotesEntity]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notesCached: NotesEntity)

    /**
     *  @param userId Long
     *  @return [NotesEntity]?
     */
    @Query("SELECT * FROM notes WHERE id = :userId")
    suspend fun getNotesByUserId(userId: Long): NotesEntity?

    /***
     *  @param input String
     *  @return List of [NotesEntity]
     */
    @Query("SELECT * FROM notes WHERE value LIKE '%' || :input || '%'")
    suspend fun searchByInput(input: String): List<NotesEntity>
}
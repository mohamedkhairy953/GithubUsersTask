package com.khairy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.khairy.database.daos.NotesDao
import com.khairy.database.daos.ProfileDao
import com.khairy.database.daos.UserDao
import com.khairy.database.entities.NotesEntity
import com.khairy.database.entities.ProfileEntity
import com.khairy.database.entities.UserEntity


@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        NotesEntity::class
    ], version = 1
)
/**
 *  Tawk_test_task app Room database
 */
abstract class AppDatabase : RoomDatabase() {

    /**
     *  @return Instance of [UserDao]
     */
    abstract fun userDao(): UserDao

    /**
     *  @return Instance of [ProfileDao]
     */
    abstract fun profileDao(): ProfileDao

    /**
     *  @return Instance of [NotesDao]
     */
    abstract fun notesDao(): NotesDao

    companion object {
        const val DATABASE_NAME: String = "app_db"
    }
}
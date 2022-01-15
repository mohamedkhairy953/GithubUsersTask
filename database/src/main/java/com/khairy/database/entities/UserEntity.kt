package com.khairy.database.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * User local data base class
 *
 * @property id Long
 * @property login String
 * @property url String
 * @property avatarUrl String
 */
@Entity(tableName = "user")
data class UserEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String
)
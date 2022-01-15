package com.khairy.shared_models.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val FIRST_USER_ID = 0L


@Parcelize
data class User(
    var id: Long,
    var login: String,
    var url: String,
    var avatarUrl: String,
    var profile: Profile? = null,
    var notes: String = ""
) : Parcelable
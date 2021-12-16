package com.example.mymessenger.utills

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import kotlin.String

@IgnoreExtraProperties
@Parcelize
data class Contact(
    val uid: String? = null,
    val email: String? = null,
    val nickname: String? = null
):Parcelable
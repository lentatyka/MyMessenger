package com.example.mymessenger.utills

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Contact(
    val email: String? = null,
    val nickname: String? = null,
    val uid: String? = null
)
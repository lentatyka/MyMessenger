package com.example.mymessenger.interfaces

import android.os.Parcelable

interface Contact:Parcelable {
    val uid: String?
    val email: String?
    val nickname: String?
    val avatar: ByteArray?
    val info: String?
    val isOwn: Boolean
}
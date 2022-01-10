package com.example.mymessenger.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CONTACTS
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import kotlin.String

@IgnoreExtraProperties
@Parcelize
data class Contact(
    val uid: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val avatar: ByteArray? = null,
    val info: String? = null,
):Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (avatar != null) {
            if (other.avatar == null) return false
            if (!avatar.contentEquals(other.avatar)) return false
        } else if (other.avatar != null) return false

        return true
    }

    override fun hashCode(): Int {
        return avatar?.contentHashCode() ?: 0
    }
}


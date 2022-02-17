package com.example.mymessenger.firebase

import android.os.Parcelable
import com.example.mymessenger.interfaces.Contact
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FirebaseContact(
    override val uid: String = "",
    override val email: String = "",
    override val nickname: String = "",
    override val avatar: ByteArray? = null,
    override val info: String? = null,
    override val isOwn: Boolean = false
) :Contact{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirebaseContact

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
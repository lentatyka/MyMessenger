package com.example.mymessenger.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CONTACTS
import kotlinx.android.parcel.Parcelize
import kotlin.String
@Entity(tableName = SQLITE_TABLE_CONTACTS)
@Parcelize
data class RoomContact(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "cuid")
    override val uid: String,
    override val email: String,
    override val nickname: String,
    override val avatar: ByteArray?,
    override val info: String?,
    override val isOwn: Boolean = false
) :Contact {
    constructor(uid: String) : this(uid, "", "", null, null, false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomContact

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
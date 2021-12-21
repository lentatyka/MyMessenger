package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.MessageStatus
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseMessage(
    override val uid: String? = null,
    override val name: String? =null,
    override val message: String? = null,
    override val status: MessageStatus? = null,
    override val timestamp: Long? = null,
    override val messageId: String? = null
):Message
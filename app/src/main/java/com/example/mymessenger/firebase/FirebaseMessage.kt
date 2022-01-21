package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.MessageStatus
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseMessage constructor(
    override val uid: String ="",
    override val message: String? = null,
    override val status: MessageStatus? = null,
    override val timestamp: Long? = null,
    override val messageId: String = ""
):Message
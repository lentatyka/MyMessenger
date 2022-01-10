package com.example.mymessenger.utills

import kotlin.String

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
    const val CONTACT_EXTRA = "CONTACT_EXTRA"
    lateinit var USER_ID: String
    lateinit var USER_NAME: String
    var CONTACT_ID: String? = null
    const val USERS_PATH = "users"
    const val CONTACT = "CONTACT"
    const val SQLITE_TABLE_CHAT = "chat"
    const val SQLITE_TABLE_CONTACTS = "contacts"
    const val SQLITE_BASE_NAME = "chatbase"
    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_PRIVATE_CHAT = "ACTION_PRIVATE_CHAT"
    const val NEW_MESSAGE = "NEW_MESSAGE"
    const val START_PRIVATE_CHAT = "START_PRIVATE_CHAT"
    const val CHANNEL_SIREN_DESCRIPTION = "CHANNEL_SIREN_DESCRIPTION"
}
package com.example.mymessenger.utills

import kotlin.String

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    lateinit var EMAIL: String
    lateinit var PASSWORD: String
    var USER_ID: String = "ATO"
    var USER_NAME: String = "NIGGA"
    lateinit var OPPOSITE: String
    const val USERS_PATH = "users"
    const val SQLITE_TABLE_CHAT = "chat"
    const val SQLITE_TABLE_LAST_MESSAGE = "lastmessage"
    const val SQLITE_BASE_NAME = "chatbase"
    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
}
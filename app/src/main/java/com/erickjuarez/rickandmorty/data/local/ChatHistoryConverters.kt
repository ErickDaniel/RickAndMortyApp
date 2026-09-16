package com.erickjuarez.rickandmorty.data.local

import androidx.room.TypeConverter
import com.erickjuarez.rickandmorty.domain.model.ChatHistoryMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatHistoryConverters {
    private val gson = Gson()
    private val messagesType = object : TypeToken<List<ChatHistoryMessage>>() {}.type

    @TypeConverter
    fun messagesToJson(messages: List<ChatHistoryMessage>): String =
        gson.toJson(messages, messagesType)

    @TypeConverter
    fun jsonToMessages(json: String): List<ChatHistoryMessage> =
        runCatching {
            gson.fromJson<List<ChatHistoryMessage>>(json, messagesType)
        }.getOrNull().orEmpty()
}

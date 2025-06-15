package com.example.sesyjka.models

data class ChatThread(
    val userId: String = "",
    val username: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)

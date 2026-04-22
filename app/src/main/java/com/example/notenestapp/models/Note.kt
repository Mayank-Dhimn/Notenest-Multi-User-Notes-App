package com.example.notenestapp.models

import java.io.Serializable

data class Note(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val content: String,
    val subject: String,
    val color: String,
    val isPinned: Int = 0,
    val createdAt: String
) : Serializable

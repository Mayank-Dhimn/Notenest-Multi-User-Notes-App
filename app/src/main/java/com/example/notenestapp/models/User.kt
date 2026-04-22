package com.example.notenestapp.models

data class User(
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val securityQuestion: String,
    val securityAnswer: String
)

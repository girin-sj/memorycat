package com.example.memorycat

enum class Ranking {
    NAME,
    LEVEL,
    SCORE,
    PROFILE
}

data class User(val name: String, val level: String, val score: String, val grade: String)
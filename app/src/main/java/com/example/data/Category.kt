package com.example.data

enum class Category {
    Polity, Economy, Environment, InternationalRelations, Science, Other;

    val displayName: String
        get() = when (this) {
            InternationalRelations -> "International Relations"
            else -> name
        }
}

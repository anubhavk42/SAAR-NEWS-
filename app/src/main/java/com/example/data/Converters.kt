package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return try {
            Category.valueOf(value)
        } catch (e: Exception) {
            Category.Other
        }
    }
}

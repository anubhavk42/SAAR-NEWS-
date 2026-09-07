package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DigestItem::class,
        EditorialItem::class,
        QuizQuestion::class,
        QuizResult::class,
        TranslationCache::class,
        ReviewScheduleItem::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val appDao: AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digest_database"
                )
                // TODO: fallbackToDestructiveMigration wipes all user data (bookmarks,
                // read state, quiz history) on any schema change. Replace with real
                // Migrations (3 -> 4 adds the unique index on digest_items(headline, date);
                // 4 -> 5 adds the unique index on editorial_items(title, date);
                // 5 -> 6 adds the review_schedule_items table)
                // before any public release.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

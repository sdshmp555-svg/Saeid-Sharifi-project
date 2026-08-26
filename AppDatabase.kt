package com.saeid.italyaiculturaltourism.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlanDao {
    @Query("SELECT * FROM saved_plans ORDER BY createdAt DESC")
    fun all(): Flow<List<SavedPlan>>

    @Insert
    suspend fun insert(plan: SavedPlan)

    @Delete
    suspend fun delete(plan: SavedPlan)
}

@Database(entities = [SavedPlan::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedPlanDao(): SavedPlanDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "italy_ai_tourism.db"
            ).build().also { INSTANCE = it }
        }
    }
}

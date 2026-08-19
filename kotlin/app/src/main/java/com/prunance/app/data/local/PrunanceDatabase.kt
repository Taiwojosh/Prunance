package com.prunance.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prunance.app.data.local.dao.BillDao
import com.prunance.app.data.local.dao.ExpenseDao
import com.prunance.app.data.local.dao.SavingsGoalDao
import com.prunance.app.data.local.entity.BillEntity
import com.prunance.app.data.local.entity.ExpenseEntity
import com.prunance.app.data.local.entity.SavingsGoalEntity

@Database(
    entities = [ExpenseEntity::class, BillEntity::class, SavingsGoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PrunanceDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun billDao(): BillDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var INSTANCE: PrunanceDatabase? = null

        fun getDatabase(context: Context): PrunanceDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PrunanceDatabase::class.java,
                        "prunance_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    android.util.Log.e("PrunanceDatabase", "Error building Room database", e)
                    // Report to analytics placeholder: Analytics.logError(e)
                    throw e // Re-throw as this is critical
                }
            }
        }
    }
}

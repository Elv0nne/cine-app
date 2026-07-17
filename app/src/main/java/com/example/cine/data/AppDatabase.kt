package com.example.cine.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val slug: String,
    val name: String,
    val posterUrl: String,
    val originName: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val slug: String,
    val name: String,
    val posterUrl: String,
    val episodeName: String,
    val episodeUrl: String,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface MovieDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun favorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE slug = :slug)")
    fun isFavorite(slug: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(f: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE slug = :slug")
    suspend fun removeFavorite(slug: String)

    @Query("SELECT * FROM history ORDER BY updatedAt DESC LIMIT 30")
    fun history(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE slug = :slug")
    suspend fun historyFor(slug: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(h: HistoryEntity)
}

@Database(
    entities = [FavoriteEntity::class, HistoryEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): MovieDao

    companion object {
        @Volatile private var I: AppDatabase? = null
        fun get(ctx: Context): AppDatabase = I ?: synchronized(this) {
            Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java,
                "cine.db"
            ).build().also { I = it }
        }
    }
}

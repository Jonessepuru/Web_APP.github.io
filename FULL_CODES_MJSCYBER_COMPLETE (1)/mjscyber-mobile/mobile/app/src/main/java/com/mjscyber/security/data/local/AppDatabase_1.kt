package com.mjscyber.security.data.local

import androidx.room.*
import com.mjscyber.security.data.models.Course

/**
 * Room – Offline cache for Bochum low bandwidth
 * Task 2: Database tables using DBMS, at least 10 records per table
 * Room entities mirror MongoDB collections for offline use
 */

// Course entity – cached from API – 10+ records
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val code: String,
    val title: String,
    val grade: String,
    val description: String,
    val durationDays: Int,
    val priceZar: Double,
    val active: Boolean
) {
    fun toCourse() = Course(id, code, title, grade, description, durationDays, priceZar, active)
    companion object {
        fun fromCourse(c: Course) = CourseEntity(c.id, c.code, c.title, c.grade, c.description, c.durationDays, c.priceZar, c.active)
    }
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE active = 1")
    suspend fun getAll(): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun clear()
}

// Certificate cache for offline verification
@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val id: String,
    val serial: String,
    val studentName: String,
    val studentEmail: String,
    val courseTitle: String,
    val courseCode: String,
    val overallMark: Int,
    val issuedAt: String
)

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates")
    suspend fun getAll(): List<CertificateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(certs: List<CertificateEntity>)

    @Query("DELETE FROM certificates")
    suspend fun clear()
}

@Database(entities = [CourseEntity::class, CertificateEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun certificateDao(): CertificateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mjscyber_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

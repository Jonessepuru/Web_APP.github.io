package com.mjscyber.security.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.local.AppDatabase
import com.mjscyber.security.data.local.CourseEntity
import com.mjscyber.security.data.models.Course
import kotlinx.coroutines.launch

/**
 * CourseViewModel – loads courses from API + caches offline in Room
 * Task 2: At least 10 records per table, offline handling for Bochum
 */
class CourseViewModel(context: Context) : ViewModel() {

    private val api = ApiClient.getApi(context)
    private val db = AppDatabase.getDatabase(context)
    private val courseDao = db.courseDao()

    var uiState by mutableStateOf(CourseUiState())
        private set

    data class CourseUiState(
        val isLoading: Boolean = false,
        val courses: List<Course> = emptyList(),
        val error: String? = null
    )

    init {
        loadCourses()
    }

    fun loadCourses() {
        Log.d("CourseVM", "loadCourses() – fetching from API")
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val remote = api.getCourses()
                Log.d("CourseVM", "API returned ${remote.size} courses")
                uiState = uiState.copy(isLoading = false, courses = remote)

                // Cache offline – Room
                val entities = remote.map { CourseEntity.fromCourse(it) }
                courseDao.clear()
                courseDao.insertAll(entities)
                Log.d("CourseVM", "Cached ${entities.size} courses in Room")

            } catch (e: Exception) {
                Log.e("CourseVM", "API failed, trying Room cache: ${e.message}")
                // Fallback to Room cache
                try {
                    val cached = courseDao.getAll().map { it.toCourse() }
                    if (cached.isNotEmpty()) {
                        uiState = uiState.copy(isLoading = false, courses = cached)
                        Log.d("CourseVM", "Loaded ${cached.size} from Room cache")
                    } else {
                        uiState = uiState.copy(isLoading = false, error = "No internet and no cached data: ${e.message}")
                    }
                } catch (roomError: Exception) {
                    uiState = uiState.copy(isLoading = false, error = "Failed to load: ${e.message}")
                }
            }
        }
    }

    fun enrol(courseId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d("CourseVM", "enrol() courseId=$courseId")
        viewModelScope.launch {
            try {
                api.enrol(mapOf("course_id" to courseId))
                Log.d("CourseVM", "Enrol success")
                onSuccess()
            } catch (e: Exception) {
                Log.e("CourseVM", "Enrol failed: ${e.message}")
                onError(e.message ?: "Enrolment failed")
            }
        }
    }
}

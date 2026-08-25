package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.data.api.ApiClient
import com.mjscyber.security.data.models.Course
import kotlinx.coroutines.launch

/**
 * CourseDetailScreen – detail + enrol button – handles invalid inputs without crashing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    onBack: () -> Unit,
    onEnrolled: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { ApiClient.getApi(context) }
    val scope = rememberCoroutineScope()

    var course by remember { mutableStateOf<Course?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var enrolling by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        try {
            course = api.getCourse(courseId)
            Log.d("CourseDetail", "Loaded course: ${course?.title}")
        } catch (e: Exception) {
            error = e.message
            Log.e("CourseDetail", "Failed to load course: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Course Detail") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            })
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                course?.let { c ->
                    Text(c.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("${c.code} · Grade ${c.grade} · ${c.durationDays} days · R ${c.priceZar}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Text("About this course", style = MaterialTheme.typography.titleSmall)
                    Text(c.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))
                    Text("What you will learn:", style = MaterialTheme.typography.titleSmall)
                    Text("• Network security basics\n• Threat detection\n• Safe practices\n• PSIRA compliance", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            enrolling = true
                            scope.launch {
                                try {
                                    api.enrol(mapOf("course_id" to courseId))
                                    Log.d("CourseDetail", "Enrol success for $courseId")
                                    enrolling = false
                                    onEnrolled()
                                } catch (e: Exception) {
                                    Log.e("CourseDetail", "Enrol failed: ${e.message}")
                                    error = e.message
                                    enrolling = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !enrolling
                    ) {
                        if (enrolling) CircularProgressIndicator(Modifier.size(20.dp))
                        else Text("ENROL NOW")
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                } ?: Text("Course not found: $error")
            }
        }
    }
}

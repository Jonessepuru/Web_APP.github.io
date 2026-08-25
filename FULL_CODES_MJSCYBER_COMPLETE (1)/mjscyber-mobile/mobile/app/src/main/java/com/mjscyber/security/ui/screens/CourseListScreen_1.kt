package com.mjscyber.security.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mjscyber.security.viewmodel.CourseViewModel

/**
 * CourseListScreen – shows all courses – 10+ records, filter by Grade
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    onCourseClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vm = remember { CourseViewModel(context) }
    val state = vm.uiState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Courses – PSIRA E-A") }, navigationIcon = {
                IconButton(onClick = onBack) { Text("←") }
            })
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.courses) { course ->
                    Card(Modifier.fillMaxWidth(), onClick = { Log.d("CourseList", "Clicked ${course.id}"); onCourseClick(course.id) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text("${course.code} – ${course.title}", style = MaterialTheme.typography.titleSmall)
                            Text("Grade ${course.grade} · ${course.durationDays} days · R ${course.priceZar}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text(course.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

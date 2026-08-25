package com.mjscyber.security.data.models

import com.squareup.moshi.Json

/**
 * Models – Shared with website backend (FastAPI)
 * Must match Pydantic models in server.py
 * Comments + logging reference for WIL
 */

// User – matches /api/auth/me response
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // student or admin
    @Json(name = "sa_id") val saId: String? = null
)

// Login/Register payloads
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @Json(name = "sa_id") val saId: String? = null
)

// Auth response – backend sets httpOnly cookies, but also returns user for mobile convenience
data class AuthResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String? = null // For mobile – store encrypted (Task 2 encrypt password/JWT)
)

// Course – matches MongoDB courses collection – 10+ records seeded
data class Course(
    val id: String,
    val code: String, // e.g., PSIRA-E
    val title: String,
    val grade: String, // E, D, C, B, A, Armed
    val description: String,
    @Json(name = "duration_days") val durationDays: Int,
    @Json(name = "price_zar") val priceZar: Double,
    val active: Boolean = true
)

// Enrolment – matches enrolments collection
data class Enrolment(
    val id: String,
    @Json(name = "student_id") val studentId: String? = null,
    @Json(name = "course_id") val courseId: String,
    val course: Course? = null,
    val status: String, // pending, active, completed, failed
    @Json(name = "theory_mark") val theoryMark: Int? = null,
    @Json(name = "practical_mark") val practicalMark: Int? = null,
    @Json(name = "overall_mark") val overallMark: Int? = null,
    @Json(name = "enrolled_at") val enrolledAt: String? = null
)

// Certificate – serial MJS-YYYY-XXXXXXXX – public verification
data class Certificate(
    val id: String,
    val serial: String, // e.g., MJS-2026-A1B2C3D4
    @Json(name = "student_name") val studentName: String,
    @Json(name = "student_email") val studentEmail: String,
    @Json(name = "course_title") val courseTitle: String,
    @Json(name = "course_code") val courseCode: String,
    @Json(name = "overall_mark") val overallMark: Int,
    @Json(name = "issued_at") val issuedAt: String,
    @Json(name = "qr_url") val qrUrl: String? = null
)

// Verify response – GET /api/verify/{serial} – no auth required
data class VerifyResponse(
    val valid: Boolean,
    val serial: String? = null,
    @Json(name = "student_name") val studentName: String? = null,
    val course: String? = null,
    val mark: Int? = null,
    @Json(name = "issued_at") val issuedAt: String? = null,
    val message: String? = null
)

// VIP Protection Request
data class VipRequest(
    @Json(name = "client_name") val clientName: String,
    val location: String,
    @Json(name = "start_date") val startDate: String, // YYYY-MM-DD
    @Json(name = "end_date") val endDate: String,
    @Json(name = "guards_needed") val guardsNeeded: Int,
    val notes: String? = null,
    val status: String? = "pending"
)

// Site Security Request
data class SiteRequest(
    @Json(name = "site_name") val siteName: String,
    val location: String,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String,
    @Json(name = "guards_needed") val guardsNeeded: Int,
    @Json(name = "shift_type") val shiftType: String = "day", // day, night, 24-7
    val notes: String? = null,
    val status: String? = "pending"
)

// Admin stats – GET /api/admin/stats
data class AdminStats(
    val students: Int,
    val courses: Int,
    @Json(name = "enrolments_pending") val enrolmentsPending: Int,
    @Json(name = "enrolments_active") val enrolmentsActive: Int,
    val certificates: Int,
    @Json(name = "vip_pending") val vipPending: Int,
    @Json(name = "site_pending") val sitePending: Int,
    @Json(name = "revenue_zar") val revenueZar: Double
)

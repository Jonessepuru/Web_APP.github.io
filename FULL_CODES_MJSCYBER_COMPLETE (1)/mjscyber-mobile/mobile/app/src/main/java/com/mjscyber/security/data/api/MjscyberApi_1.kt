package com.mjscyber.security.data.api

import com.mjscyber.security.data.models.*
import retrofit2.http.*

/**
 * MjscyberApi – Retrofit interface to FastAPI backend (shared with website)
 * Task 2: Must show what data is stored in online-hosted auth service, API, and database
 * Backend: https://api.mjscyber.co.za/api or http://10.0.2.2:8001/api for emulator
 */
interface MjscyberApi {

    // Auth – Task 2: register/login with encrypted password (bcrypt on backend)
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(): Map<String, String>

    @GET("auth/me")
    suspend fun getMe(): User

    @POST("auth/refresh")
    suspend fun refresh(): Map<String, String>

    // Courses – public, 10+ records
    @GET("courses")
    suspend fun getCourses(): List<Course>

    @GET("courses/{id}")
    suspend fun getCourse(@Path("id") id: String): Course

    // Enrolments – student
    @POST("enrolments")
    suspend fun enrol(@Body body: Map<String, String>): Enrolment

    @GET("enrolments/mine")
    suspend fun getMyEnrolments(): List<Enrolment>

    // Certificates – student
    @GET("certificates/mine")
    suspend fun getMyCertificates(): List<Certificate>

    // Verify – PUBLIC, no auth – Task 2 innovative: QR verification
    @GET("verify/{serial}")
    suspend fun verifyCertificate(@Path("serial") serial: String): VerifyResponse

    // VIP & Site – student can request
    @POST("vip-requests")
    suspend fun createVipRequest(@Body req: VipRequest): Map<String, String>

    @POST("site-security")
    suspend fun createSiteRequest(@Body req: SiteRequest): Map<String, String>

    @GET("vip-requests/mine")
    suspend fun getMyVipRequests(): List<VipRequest>

    @GET("site-security/mine")
    suspend fun getMySiteRequests(): List<SiteRequest>

    // Admin – only if role=admin
    @GET("admin/stats")
    suspend fun getAdminStats(): AdminStats

    @GET("enrolments")
    suspend fun getAllEnrolments(): List<Enrolment>

    @GET("admin/students")
    suspend fun getStudents(): List<User>
}

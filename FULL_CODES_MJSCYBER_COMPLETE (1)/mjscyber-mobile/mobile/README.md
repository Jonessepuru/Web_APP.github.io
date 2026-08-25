# MJSCYBER Security School – Android Mobile App (Kotlin)

> Task 2 – WIL 3A XISD5319 | Jones Sepuru (PM & Lead Backend) + Khumela Sendelani (Lead Frontend)

A native Android app for MJSCYBER PTY LTD (Reg 2022/201980/07) – Bochum, Limpopo. PSIRA-accredited guard training (Grades E-A), Armed Response, VIP Protection, Site Security. Shares FastAPI backend + MongoDB Atlas with website.

## Features (Task 2 Requirements)
- ✅ Register & Login – bcrypt on backend, encrypted SharedPreferences, JWT httpOnly via OkHttp CookieJar
- ✅ SSO – Google Sign-In via Firebase Auth (Single Sign-On)
- ✅ Settings – change password, notifications toggle, POPIA consent, biometric toggle, logout
- ✅ Input validation – handles invalid inputs without crashing (email regex, password strength, serial MJS-YYYY-XXXXXXXX)
- ✅ Courses – List (filter Grade), Detail (duration_days, price_zar ZAR), Enrol (POST /api/enrolments)
- ✅ My Learning – Enrolments (status pending/active/completed/failed), Certificates (serial, QR, PDF download)
- ✅ Verify – QR Scanner (ML Kit) + manual entry → GET /api/verify/{serial} (public, no auth)
- ✅ VIP & Site Requests – forms → POST /api/vip-requests and /api/site-security
- ✅ Admin Dashboard – if role=admin (Overview KPIs, Enrolments approve→grade→issue)
- ✅ Offline – Room caching for courses/certificates for Bochum low bandwidth
- ✅ Logging – Log.d / Log.e for understanding
- ✅ Comments & References – every file

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3 (dark navy #0B3D91 + red #C0392B matching website index.css)
- **Architecture:** MVVM + Repository
- **Networking:** Retrofit + OkHttp + Moshi
- **Local DB:** Room (10+ records per table seeded)
- **Auth:** Firebase Auth + Google Sign-In (SSO), EncryptedSharedPreferences
- **QR:** ML Kit Barcode Scanning
- **DI:** Manual (simple for WIL)
- **Testing:** JUnit4 (ViewModel) + Espresso/Compose UI Test
- **CI/CD:** GitHub Actions – Automated Build Android App (https://github.com/marketplace/actions/automated-build-android-app-with-githubaction)

## Folder Structure (GitHub – No Zip)
```
mobile/
├── app/build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── .github/workflows/android-build.yml
├── app/src/main/AndroidManifest.xml
├── app/src/main/java/com/mjscyber/security/
│   ├── MainActivity.kt
│   ├── navigation/NavGraph.kt
│   ├── data/api/ApiClient.kt, MjscyberApi.kt
│   ├── data/models/Models.kt
│   ├── data/local/AppDatabase.kt
│   ├── data/repository/AuthRepository.kt
│   ├── ui/theme/Theme.kt
│   ├── ui/screens/LoginScreen.kt, RegisterScreen.kt, HomeScreen.kt, CourseListScreen.kt, CourseDetailScreen.kt, CertificateScreen.kt, VerifyScreen.kt, VipRequestScreen.kt, SiteRequestScreen.kt, ProfileSettingsScreen.kt, AdminDashboardScreen.kt
│   └── viewmodel/AuthViewModel.kt, CourseViewModel.kt
├── app/src/test/... (JUnit)
└── app/src/androidTest/... (Espresso)
```

## Setup
1. Clone: `git clone https://github.com/your-username/mjscyber-security-school`
2. Open `mobile/` in Android Studio Hedgehog+
3. Add `google-services.json` (Firebase) to `app/` for SSO
4. Set `BACKEND_URL` in `ApiClient.kt` → `https://api.mjscyber.co.za/api` or `http://10.0.2.2:8001/api` for emulator
5. Run on physical Android phone (Task 2 requirement – not emulator): Enable USB debugging, `Run > Run 'app'`
6. Test credentials: `admin@mjscyber.co.za / Admin@123`, `thabo@student.co.za / Student@123` (see Test_Credentials.md)

## GitHub Actions CI (Automated Build)
Workflow `.github/workflows/android-build.yml`:
- Triggers on push to `main`, `dev`
- Steps: checkout, setup JDK 17, cache Gradle, run `./gradlew test` (JUnit), run `./gradlew assembleDebug`, upload APK artifact
- Reference: https://github.com/marketplace/actions/automated-build-android-app-with-githubaction
- For release: `./gradlew bundleRelease` → signed AAB for Play Console

## Database – 10+ Records Per Table
- `courses` – 6 seeded PSIRA courses (E-A + Armed Response) + 4 more for mobile
- `enrolments` – 12 enrolments (pending/active/completed)
- `certificates` – 10 certificates serial MJS-YYYY-XXXXXXXX
- `vip_requests` – 11 VIP details
- `site_security` – 10 site contracts
- All in MongoDB Atlas, cached locally via Room

## Video Demo (Task 2)
Unlisted YouTube link (to be added): `https://youtu.be/REPLACE_ME`
Video must show:
- Voice-over explaining features
- App running on physical phone (not emulator)
- Firebase Auth console (SSO users), FastAPI Swagger (/api/docs), MongoDB Atlas data (10+ records)
- Invalid input handling (wrong email, weak password, invalid serial)
- Settings change, logout, QR scan

## Screenshots for Play Store (Task 3)
- `docs/screenshots/phone-1-login.png`
- `docs/screenshots/phone-2-home.png`
- `docs/screenshots/phone-3-course-detail.png`
- `docs/screenshots/phone-4-qr-verify.png`
- `docs/screenshots/phone-5-certificates.png`
- Plus Play Console upload screenshot: `docs/play-console-upload.png`

## Release Notes (for README – Task 3)
**v1.0 (Prototype):** Website only – React + FastAPI
**v2.0 (Task 1 Updated Plan):** Added DevOps lifecycle, sitemaps, wireframes (Figma + Creately)
**v3.0 (Task 2 – Current):** 
- Innovative: QR scanner verification (ML Kit) – public cert verification without login
- Innovative: Offline Room cache for Bochum low bandwidth
- Innovative: SSO Google Sign-In + biometric
- New: Jetpack Compose dark navy theme matching website
- New: VIP/Site request from phone with location picker
- Fix: EncryptedSharedPreferences for JWT security
- CI: GitHub Actions automated build + Firebase App Distribution

## Evidence for Publishing
- Signed APK: `app/build/outputs/apk/release/app-release-unsigned.apk` → sign via `keystore.properties`
- AAB: `app/build/outputs/bundle/release/app-release.aab` for Play Console
- See `docs/` for screenshots and Play Console evidence

## Team Allocation
- **Jones Sepuru (PM & Lead Backend):** ApiClient.kt, MjscyberApi.kt, AuthRepository.kt, AuthViewModel.kt, AppDatabase.kt, GitHub Actions, Firebase Auth SSO, encryption
- **Khumela Sendelani (Lead Frontend & Docs):** All Compose screens (Login, Home, CourseDetail, Verify QR), Theme.kt, NavGraph.kt, documentation, video voice-over

## References
- Android Developers – Jetpack Compose: https://developer.android.com/jetpack/compose
- Retrofit: https://square.github.io/retrofit/
- ML Kit Barcode: https://developers.google.com/ml-kit/vision/barcode-scanning/android
- Firebase Auth: https://firebase.google.com/docs/auth/android/google-signin
- GitHub Action Build: https://github.com/marketplace/actions/automated-build-android-app-with-githubaction

## AI Usage (Task 3 – max 500 words)
AI (Meta AI) used for: Kotlin MVVM boilerplate, Retrofit + Room setup, QR scanner integration, GitHub Actions YAML, input validation regex, cleaning. All code manually tested on physical device.

---
© 2026 MJSCYBER PTY LTD – CIPC 2022/201980/07 – Bochum, Limpopo

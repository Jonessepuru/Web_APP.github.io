# MJSCYBER – GitHub Actions Setup Guide
# Reference: https://github.com/marketplace/actions/automated-build-android-app-with-githubaction

## How this workflow satisfies Task 2

**Task 2 says:** "Conduct automated testing on the main functionality of your app. Make use of GitHub Actions to run tests and build your code to make sure it will work not just on your computer. Use this guide: https://github.com/marketplace/actions/automated-build-android-app-with-githubaction"

This file `.github/workflows/android-build.yml` does exactly that:

1. **Triggers:** On push to main/dev/feature branches, only when mobile/** changes (saves minutes)
2. **Tests:** `./gradlew testDebugUnitTest` – runs JUnit tests AuthViewModelTest.kt (email validation, serial regex MJS-YYYY-XXXXXXXX, password strength)
3. **Build:** `./gradlew assembleDebug` – builds debug APK
4. **Artifacts:** Uploads APK – you can download from GitHub Actions tab and install on physical phone (Task 2 says app must run on mobile phone not emulator)
5. **Release:** On main branch, if keystore.properties exists, builds bundleRelease AAB for Google Play Store (Task 3 publishing evidence)

## Setup for your repo

1. Create repo: `mjscyber-security-school`
2. Copy this `mobile/` folder into repo root
3. Ensure `mobile/gradlew` is executable: `chmod +x mobile/gradlew` (workflow does this too)
4. Push: GitHub Actions will auto-run – check Actions tab
5. Download APK artifact → install on physical Android phone via USB or Firebase App Distribution

## For Signed APK/AAB (Task 3 – Play Store evidence)

Create `mobile/app/keystore.properties` (DO NOT COMMIT):
```
storeFile=/path/to/mjscyber.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=mjscyber
keyPassword=YOUR_KEY_PASSWORD
```

And in GitHub repo Settings → Secrets and variables → Actions, add:
- KEYSTORE_PASSWORD
- KEY_ALIAS
- KEY_PASSWORD
- KEYSTORE_BASE64 (base64 of your .jks file)

Then add step to decode keystore in workflow (see Android docs).

## For Firebase SSO (Task 2 SSO)

1. Go to Firebase Console → Add project MJSCYBER
2. Add Android app – package com.mjscyber.security
3. Download google-services.json → place in mobile/app/
4. Enable Authentication → Sign-in method → Google
5. For physical device testing: Firebase App Distribution – upload APK

## Video Demo Checklist (Task 2)

Your video must show (with voice-over):
- App running on physical phone (show phone in hand, not emulator)
- Firebase Auth console – users list (SSO users)
- FastAPI /api/docs Swagger – show endpoints
- MongoDB Atlas – show 10+ records per table (courses, enrolments, certificates, vip_requests, site_security)
- Invalid input handling: try wrong email format, weak password, invalid serial MJS-... – app must not crash
- Settings change: toggle notifications, biometric, change password
- QR verification: scan a real certificate QR
- GitHub Actions tab – show workflow passing

Upload video to YouTube unlisted, add link to mobile/README.md and main README.md

## PowerPoint (Task 3)

Each team member must talk through at least one topic:
- Jones: DevOps lifecycle, backend API, security (bcrypt, JWT, encrypted prefs), GitHub Actions
- Khumela: Wireframes, sitemaps, Compose UI, QR scanner, settings, video demo

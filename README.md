# Call Security Agent - Android App

## Quick Start Guide

### Prerequisites
- **Android Studio** (Download: https://developer.android.com/studio)
- **Android Phone** with Android 10+ (API 29+)
- **USB Cable** for connecting phone to computer

### Installation Steps

#### 1. Open Project in Android Studio

```bash
# Navigate to the android-app directory
cd android-app

# Open Android Studio and select "Open an Existing Project"
# Navigate to this android-app folder and click OK
```

#### 2. Sync Gradle

- Android Studio will automatically start syncing Gradle
- Wait for "Gradle sync finished" message (may take 2-5 minutes first time)
- If you see errors, click "File" → "Sync Project with Gradle Files"

#### 3. Connect Your Phone

1. **Enable Developer Mode** on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - You'll see "You are now a developer!"

2. **Enable USB Debugging**:
   - Go to Settings → Developer Options
   - Enable "USB Debugging"

3. **Connect via USB**:
   - Plug phone into computer
   - On phone, tap "Allow USB Debugging" when prompted

4. **Verify Connection**:
   - In Android Studio, check top toolbar
   - You should see your phone model in the device dropdown

#### 4. Run the App

1. Click the green **Play** button ▶️ in Android Studio toolbar
2. Select your phone from the device list
3. Click "OK"
4. App will build and install (takes 1-2 minutes first time)

#### 5. Setup on Phone

Once app opens on your phone:

1. **Grant Permissions**:
   - Tap "Grant Permissions" button
   - Allow all 4 permissions when prompted:
     - Phone
     - Call Log
     - Contacts
     - (Phone Calls will be auto-granted)

2. **Enable Call Screening**:
   - Tap "Open Phone Settings"
   - Find "Call Blocking & Identification" or "Spam Protection"
   - Enable "Call Security Agent"
   - (Location varies by phone manufacturer)

3. **You're Protected!** 🛡️
   - The app is now actively screening calls
   - Spam calls will be blocked automatically
   - Your contacts will never be blocked

### Testing

Test with these scenarios:

1. **Test Spam Number**: Call from 1-800-555-1234 → Should block
2. **Test Contact**: Call from saved contact → Should allow
3. **Test Unknown**: Call from random number → Check classification

View logs in Android Studio:
```
Logcat → Filter: "CallSecurity"
```

### Troubleshooting

**App won't install?**
- Make sure USB Debugging is enabled
- Try different USB cable/port
- Run: `adb devices` in terminal to verify connection

**Permissions not working?**
- Go to Phone Settings → Apps → Call Security Agent
- Manually enable all permissions

**Calls not being screened?**
- Check Phone Settings → Call Blocking
- Make sure Call Security Agent is enabled
- Restart phone if needed

**Build errors?**
- File → Invalidate Caches → Invalidate and Restart
- Tools → SDK Manager → Install latest Android SDK

### Project Structure

```
android-app/
├── app/
│   ├── build.gradle                 # Dependencies
│   └── src/main/
│       ├── AndroidManifest.xml      # Permissions & services
│       ├── java/com/callsecurity/agent/
│       │   ├── service/
│       │   │   └── CallSecurityScreeningService.kt  # Call interception
│       │   ├── core/
│       │   │   └── CallClassifier.kt                # Spam detection
│       │   ├── ui/
│       │   │   └── MainActivity.kt                  # Setup UI
│       │   └── CallSecurityApplication.kt
│       └── res/
│           ├── layout/
│           │   └── activity_main.xml                # UI layout
│           └── values/
│               └── strings.xml
└── build.gradle                     # Project config
```

### How It Works

1. **Incoming Call** → Android system calls `CallSecurityScreeningService`
2. **Classification** → `CallClassifier` analyzes the number:
   - Pattern matching (toll-free, sequential, repeated digits)
   - Contact list check
   - Behavioral analysis
3. **Decision** → If spam score ≥ 70%: **BLOCK** (aggressive mode)
4. **Response** → Call is blocked silently or allowed

### Customization

Edit thresholds in `CallClassifier.kt`:

```kotlin
private const val SPAM_HIGH_THRESHOLD = 90f      // High confidence
private const val SPAM_LIKELY_THRESHOLD = 70f    // Likely spam (aggressive)
private const val SPAM_UNCERTAIN_THRESHOLD = 40f // Uncertain
```

### Next Steps

- [ ] Add Room database for threat intelligence
- [ ] Implement cloud sync
- [ ] Add TensorFlow Lite ML model
- [ ] Create settings screen
- [ ] Add statistics dashboard

### Support

For issues:
1. Check Logcat for errors
2. Verify all permissions granted
3. Test with known spam numbers
4. Check phone's call blocking settings

---

**🎉 Congratulations!** You now have a working spam call blocker on your phone!

# 📧 Automated Email Script - Quick Guide

## How to Use

### Step 1: Open Terminal
- Press `Cmd + Space`
- Type "Terminal"
- Press Enter

### Step 2: Navigate to Project
```bash
cd /Users/daironrodriguez/.gemini/antigravity/playground/glacial-corona/android-app
```

### Step 3: Run the Script
```bash
./build-and-email.sh your-email@gmail.com
```

**Replace `your-email@gmail.com` with your actual email!**

---

## What It Does

1. ✅ Builds the APK file
2. ✅ Attempts to email it to you
3. ✅ Opens the folder with the APK
4. ✅ Shows you alternative methods if email fails

---

## If Email Doesn't Work Automatically

The script will show you the APK location. Then:

### **Option A: Right-Click Method**
1. Find the APK in the opened Finder window
2. Right-click `app-debug.apk`
3. Click **Share** → **Mail**
4. Enter your email
5. Send!

### **Option B: Gmail in Browser**
1. Open https://gmail.com
2. Click "Compose"
3. Drag the APK file into the email
4. Send to yourself

### **Option C: Google Drive**
1. Open https://drive.google.com
2. Click "New" → "File Upload"
3. Select the APK
4. Download on your phone from Drive app

---

## Complete Example

```bash
# Navigate to project
cd /Users/daironrodriguez/.gemini/antigravity/playground/glacial-corona/android-app

# Build and email (replace with your email)
./build-and-email.sh myemail@gmail.com
```

---

## On Your Phone

Once you receive the email:

1. **Open the email** on your Android phone
2. **Download the attachment** (app-debug.apk)
3. **Tap the downloaded file**
4. **Enable "Install from Unknown Sources"** if asked
5. **Tap "Install"**
6. **Done!** 🎉

---

## Troubleshooting

**"Permission denied":**
```bash
chmod +x build-and-email.sh
./build-and-email.sh your-email@gmail.com
```

**Email not sending:**
- Use Option A, B, or C above
- The APK location will be shown in Terminal

**Can't find APK:**
- Look in the Finder window that opens
- Or go to: `android-app/app/build/outputs/apk/debug/`

---

## Quick Reference

**Just run this (replace email):**
```bash
cd /Users/daironrodriguez/.gemini/antigravity/playground/glacial-corona/android-app
./build-and-email.sh YOUR_EMAIL@gmail.com
```

That's it! 📧

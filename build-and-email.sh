#!/bin/bash

# SUPER SIMPLE BUILD AND EMAIL SCRIPT
# Just run this and it does everything!

echo "🚀 Call Security Agent - Build & Email"
echo "======================================"
echo ""

# Email address
EMAIL="dairolrpichardo90@yahoo.com"

echo "📧 Will email to: $EMAIL"
echo ""

# Find the correct directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "📁 Working in: $SCRIPT_DIR"
echo ""

# Check if gradlew exists
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found!"
    echo "Current directory: $(pwd)"
    echo "Files here:"
    ls -la
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

echo "🔨 Building APK (this takes 2-5 minutes)..."
echo ""

# Build the APK
./gradlew assembleDebug --quiet

# Check if build succeeded
APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ Build failed! APK not found."
    echo "Expected location: $APK_PATH"
    exit 1
fi

echo "✅ Build successful!"
echo ""

# Get file size
APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
echo "📦 APK Size: $APK_SIZE"
echo "📁 APK Location: $APK_PATH"
echo ""

# Open the folder
echo "📂 Opening folder with APK..."
open "$(dirname "$APK_PATH")"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ APK READY!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📧 TO EMAIL IT TO YOURSELF:"
echo ""
echo "1. In the Finder window that just opened:"
echo "   → Right-click 'app-debug.apk'"
echo "   → Click 'Share' → 'Mail'"
echo "   → Type: $EMAIL"
echo "   → Click Send"
echo ""
echo "2. OR drag the APK file into Yahoo Mail:"
echo "   → Open https://mail.yahoo.com"
echo "   → Click 'Compose'"
echo "   → Drag app-debug.apk into the email"
echo "   → Send to: $EMAIL"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📱 ON YOUR ANDROID PHONE:"
echo "   1. Open the email"
echo "   2. Download the APK"
echo "   3. Tap to install"
echo "   4. Done! 🎉"
echo ""

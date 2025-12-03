#!/bin/bash

# BUILD AND INSTALL DIRECTLY TO PHONE VIA USB
# This is the FASTEST method!

echo "🚀 Call Security Agent - Direct Install"
echo "========================================"
echo ""

# Find the correct directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "📁 Working in: $SCRIPT_DIR"
echo ""

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
    exit 1
fi

echo "✅ Build successful!"
echo ""

# Get file size
APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
echo "📦 APK Size: $APK_SIZE"
echo ""

# Check if ADB is available
if command -v adb &> /dev/null; then
    echo "📱 Checking for connected Android devices..."
    echo ""
    
    # Check for devices
    DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)
    
    if [ $DEVICES -gt 0 ]; then
        echo "✅ Android device detected!"
        echo ""
        echo "📲 Installing APK to your phone..."
        
        adb install -r "$APK_PATH"
        
        if [ $? -eq 0 ]; then
            echo ""
            echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            echo "✅ APP INSTALLED ON YOUR PHONE!"
            echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            echo ""
            echo "📱 NEXT STEPS ON YOUR PHONE:"
            echo "   1. Open 'Call Security Agent' app"
            echo "   2. Tap 'Grant Permissions'"
            echo "   3. Allow all permissions"
            echo "   4. Tap 'Open Phone Settings'"
            echo "   5. Enable 'Call Security Agent' in Call Blocking"
            echo "   6. Done! You're protected! 🛡️"
            echo ""
        else
            echo ""
            echo "❌ Installation failed!"
            echo ""
            echo "Try these steps:"
            echo "1. On your phone: Settings → Developer Options"
            echo "2. Enable 'USB Debugging'"
            echo "3. When prompted, tap 'Allow USB Debugging'"
            echo "4. Run this script again"
        fi
    else
        echo "❌ No Android device detected!"
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "📱 CONNECT YOUR PHONE:"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "1. Connect phone to Mac via USB cable"
        echo ""
        echo "2. On your phone:"
        echo "   → Settings → About Phone"
        echo "   → Tap 'Build Number' 7 times"
        echo "   → Go back → Developer Options"
        echo "   → Enable 'USB Debugging'"
        echo ""
        echo "3. When prompted on phone:"
        echo "   → Tap 'Allow USB Debugging'"
        echo "   → Check 'Always allow from this computer'"
        echo ""
        echo "4. Run this script again:"
        echo "   bash install-to-phone.sh"
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "📧 OR EMAIL IT INSTEAD:"
        echo "   → Open Finder (folder will open)"
        echo "   → Right-click 'app-debug.apk'"
        echo "   → Share → Mail"
        echo "   → Send to: dairolrpichardo90@yahoo.com"
        echo ""
        
        # Open the folder
        open "$(dirname "$APK_PATH")"
    fi
else
    echo "⚠️  ADB not installed"
    echo ""
    echo "Installing ADB (Android Debug Bridge)..."
    echo ""
    
    # Check if Homebrew is installed
    if command -v brew &> /dev/null; then
        brew install android-platform-tools
        echo ""
        echo "✅ ADB installed! Run this script again:"
        echo "   bash install-to-phone.sh"
    else
        echo "❌ Homebrew not found"
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "📧 USE EMAIL METHOD INSTEAD:"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "1. In the Finder window that opens:"
        echo "   → Right-click 'app-debug.apk'"
        echo "   → Click 'Share' → 'Mail'"
        echo "   → Type: dairolrpichardo90@yahoo.com"
        echo "   → Click Send"
        echo ""
        echo "2. On your phone:"
        echo "   → Open email"
        echo "   → Download APK"
        echo "   → Tap to install"
        echo ""
        
        # Open the folder
        open "$(dirname "$APK_PATH")"
    fi
fi

echo ""
echo "📁 APK Location: $APK_PATH"
echo ""

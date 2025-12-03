#!/bin/bash

# FINAL SIMPLE SOLUTION
# This will work!

echo "🚀 Building Call Security Agent APK"
echo "===================================="
echo ""

cd "$(dirname "$0")"

# Check if we have Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found!"
    echo ""
    echo "Installing Java..."
    brew install openjdk@17
    
    export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)
fi

echo "✅ Java found"
echo ""

# Make sure gradlew is executable
chmod +x gradlew

echo "🔨 Building APK..."
echo "This will take 2-5 minutes..."
echo ""

# Build
./gradlew assembleDebug

APK="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK" ]; then
    echo ""
    echo "✅ SUCCESS!"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📱 APK READY!"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "📁 Location: $(pwd)/$APK"
    echo "📦 Size: $(ls -lh $APK | awk '{print $5}')"
    echo ""
    echo "📧 TO EMAIL IT:"
    echo "   1. Right-click the file in Finder"
    echo "   2. Share → Mail"
    echo "   3. Send to: dairolrpichardo90@yahoo.com"
    echo ""
    
    # Open folder
    open "$(dirname $APK)"
else
    echo ""
    echo "❌ Build failed"
    echo ""
    echo "📧 ALTERNATIVE: Use GitHub Actions"
    echo ""
    echo "1. Go to: https://github.com"
    echo "2. Create account (free)"
    echo "3. Create new repository"
    echo "4. Upload this folder"
    echo "5. GitHub builds it automatically"
    echo "6. Download APK from Actions tab"
    echo ""
fi

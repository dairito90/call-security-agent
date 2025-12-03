#!/bin/bash

# Easy Install Script for Mac (No Android Studio)
# This script builds the APK using command line tools only

set -e  # Exit on error

echo "🚀 Call Security Agent - Easy Build Script"
echo "=========================================="
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "📦 Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
else
    echo "✅ Homebrew already installed"
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "☕ Installing Java 17..."
    brew install openjdk@17
    
    # Link Java
    sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
else
    echo "✅ Java already installed"
fi

# Set Java home
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "Java version:"
java -version

# Check if Android SDK is installed
if [ ! -d "$HOME/Library/Android/sdk" ]; then
    echo "📱 Installing Android SDK..."
    brew install --cask android-commandlinetools
    
    # Set up Android SDK
    export ANDROID_HOME=$HOME/Library/Android/sdk
    export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
    export PATH=$PATH:$ANDROID_HOME/platform-tools
    
    # Accept licenses
    yes | sdkmanager --licenses
    
    # Install required components
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
else
    echo "✅ Android SDK already installed"
fi

# Navigate to project directory
cd "$(dirname "$0")"

echo ""
echo "🔨 Building APK..."
echo ""

# Make gradlew executable
chmod +x gradlew

# Build the APK
./gradlew assembleDebug --stacktrace

echo ""
echo "✅ Build complete!"
echo ""
echo "📱 APK Location:"
echo "   $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "📲 Next steps:"
echo "   1. Transfer app-debug.apk to your Android phone"
echo "   2. Open the APK file on your phone"
echo "   3. Tap 'Install'"
echo ""
echo "💡 Transfer methods:"
echo "   - Email the APK to yourself"
echo "   - Upload to Google Drive"
echo "   - Use ADB: adb install app/build/outputs/apk/debug/app-debug.apk"
echo ""

# Open the folder containing the APK
open app/build/outputs/apk/debug/

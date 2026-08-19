# Prunance Android Development Setup

## Prerequisites

### Required Software Versions

| Tool | Version | Notes |
|------|---------|-------|
| **JDK** | 17 (Temurin/OpenJDK) | Required for Android Gradle Plugin 8.x |
| **Android Studio** | Ladybug (2024.2.1) or newer | Includes Android SDK |
| **Android SDK** | API 34 (compileSdk), API 24 (minSdk) | Installed via Android Studio |
| **Gradle** | 8.5+ (via wrapper) | `./gradlew` handles version |
| **Kotlin** | 1.9.20+ | Managed by Gradle plugin |

### Installation Commands

**macOS (Homebrew):**
```bash
brew install --cask android-studio
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
# Android Studio: https://developer.android.com/studio
```

**Windows (Scoop/Chocolatey):**
```powershell
scoop install temurin17-jdk android-studio
# or
choco install temurin17 androidstudio
```

### Environment Variables

Add to `~/.bashrc`, `~/.zshrc`, or Windows Environment Variables:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # macOS
# export JAVA_HOME=/usr/lib/jvm/java-17-openjdk  # Linux
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

## Project Setup

```bash
# Clone
git clone https://github.com/Taiwojosh/Prunance.git
cd Prunance/kotlin

# Make wrapper executable (one-time)
chmod +x gradlew

# Verify environment
./gradlew --version
./gradlew dependencies --configuration debugCompileClasspath
```

## Common Commands

| Task | Command |
|------|---------|
| Clean build | `./gradlew clean` |
| Debug APK | `./gradlew assembleDebug` |
| Release APK | `./gradlew assembleRelease` |
| Run unit tests | `./gradlew test` |
| Run instrumented tests | `./gradlew connectedAndroidTest` |
| Lint check | `./gradlew lint` |
| Generate docs | `./gradlew dokkaHtml` |

## Output Locations

| Artifact | Path |
|----------|------|
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Release APK | `app/build/outputs/apk/release/app-release.apk` |
| Test reports | `app/build/reports/tests/` |
| Lint report | `app/build/reports/lint-results.html` |

## IDE Setup (Android Studio)

1. **File → Open** → Select `Prunance/kotlin` folder
2. Wait for Gradle sync (first sync downloads dependencies)
3. **Build → Make Project** (⌘F9 / Ctrl+F9)
4. **Run → Run 'app'** to install on device/emulator

## Troubleshooting

### "JAVA_HOME not set"
```bash
# Find Java 17 location
/usr/libexec/java_home -v 17  # macOS
readlink -f $(which java) | sed 's:/bin/java::'  # Linux

# Set permanently
echo 'export JAVA_HOME=/path/to/jdk17' >> ~/.zshrc
```

### "SDK location not found"
```bash
# Create local.properties in kotlin/
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

### Gradle daemon issues
```bash
./gradlew --stop
./gradlew clean assembleDebug --no-daemon
```

### OutOfMemoryError
Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

## CI/CD

GitHub Actions workflow runs automatically on push to `main` and PRs:
- Builds debug APK
- Runs unit tests
- Uploads APK as artifact (retention: 7 days)

See `.github/workflows/android.yml` for details.

## Architecture Notes

- **Compose** for UI (Material 3)
- **Room** for local database (expenses, bills, savings goals)
- **DataStore** for user preferences
- **Hilt** not used — manual DI via Repository pattern
- **Flow** for reactive data streams
- **ViewModel** + StateFlow for state management

## Version Catalog

Dependencies managed in `build.gradle` (no separate `libs.versions.toml` yet). Key versions:
- AGP: 8.3.0
- Kotlin: 1.9.20
- Compose BOM: 2023.08.00
- Room: 2.6.1
- DataStore: 1.0.0
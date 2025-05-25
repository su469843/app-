@echo off
chcp 65001>nul
echo ===== Weather App Local Build Script =====
echo.

echo 1. Checking JAVA_HOME environment variable...
if "%JAVA_HOME%"=="" (
  echo ERROR: JAVA_HOME environment variable is not set!
  echo Please set the JAVA_HOME variable to your JDK installation directory
  echo Example: set JAVA_HOME=C:\Program Files\Java\jdk-17
  goto :error
) else (
  echo JAVA_HOME is set to: %JAVA_HOME%
)

echo.
echo 2. Configuring Gradle properties...
echo # Optimized Gradle properties > gradle.properties.tmp
echo org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+HeapDumpOnOutOfMemoryError >> gradle.properties.tmp
echo org.gradle.parallel=true >> gradle.properties.tmp
echo org.gradle.caching=true >> gradle.properties.tmp
echo android.useAndroidX=true >> gradle.properties.tmp
echo android.enableJetifier=false >> gradle.properties.tmp
echo kotlin.incremental=false >> gradle.properties.tmp
echo android.nonTransitiveRClass=true >> gradle.properties.tmp
echo kotlin.code.style=official >> gradle.properties.tmp
echo android.enableR8.fullMode=false >> gradle.properties.tmp
move /y gradle.properties.tmp gradle.properties
echo Gradle properties updated

echo.
echo 3. Checking update file...
if not exist app\updat\updat.md (
  echo Creating default update file...
  mkdir app\updat 2>nul
  echo v1.0.0 > app\updat\updat.md
  echo # Weather Forecast App >> app\updat\updat.md
  echo Initial release >> app\updat\updat.md
)

echo.
echo 4. Building the app...
call gradlew.bat clean assembleDebug --stacktrace --info
if %ERRORLEVEL% NEQ 0 (
  echo Build failed, check error logs
  goto :error
)

echo.
echo 5. Locating APK file...
for /r %%i in (*.apk) do (
  echo Found APK: %%i
  mkdir release 2>nul
  copy "%%i" release\hello-world-app.apk
)

echo.
echo ===== Build Complete! =====
echo APK file location: %CD%\release\hello-world-app.apk
goto :end

:error
echo.
echo ===== Build Failed! =====
exit /b 1

:end
echo.
echo Press any key to exit...
pause > nul 
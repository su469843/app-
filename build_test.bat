@echo off
echo ====================================
echo Building with detailed logging...
echo ====================================

echo Cleaning project...
call ./gradlew clean

echo.
echo Running Debug build with verbose output...
call ./gradlew assembleDebug --info --stacktrace > build_debug.log 2>&1

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Debug build FAILED! Check build_debug.log for details.
    exit /b %ERRORLEVEL%
) else (
    echo Debug build completed successfully!
)

echo.
echo ====================================
echo Build successful!
echo ==================================== 
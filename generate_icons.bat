@echo off
chcp 65001>nul
echo ===== Weather App Icon Generator =====
echo.

echo Ensuring icon directories exist...
mkdir app\src\main\res\mipmap-hdpi 2>nul
mkdir app\src\main\res\mipmap-mdpi 2>nul
mkdir app\src\main\res\mipmap-xhdpi 2>nul
mkdir app\src\main\res\mipmap-xxhdpi 2>nul
mkdir app\src\main\res\mipmap-xxxhdpi 2>nul

echo.
echo Please select an option:
echo 1. Use Android Studio's Image Asset Studio (Recommended)
echo 2. Copy placeholder icon files (Temporary)
echo.
set /p choice=Enter choice (1 or 2): 

if "%choice%"=="1" (
  echo.
  echo Please follow these steps:
  echo 1. Open your project in Android Studio
  echo 2. Right-click on the "res" folder
  echo 3. Select "New > Image Asset"
  echo 4. Choose "Launcher Icons (Adaptive & Legacy)"
  echo 5. For foreground, use existing ic_launcher_foreground.xml
  echo 6. For background, use existing ic_launcher_background.xml
  echo 7. Click "Next" and "Finish" to generate all icons
  echo.
  echo After completing these steps, all necessary icon files will be generated
  echo.
  echo NOTE: After generating icons, run clean_icons.bat to resolve any conflicts
) else if "%choice%"=="2" (
  echo.
  echo Copying placeholder icon files...
  
  echo Creating placeholder ic_launcher.png files...
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-hdpi\ic_launcher.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-mdpi\ic_launcher.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xhdpi\ic_launcher.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xxhdpi\ic_launcher.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xxxhdpi\ic_launcher.png
  
  echo Creating placeholder ic_launcher_round.png files...
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-hdpi\ic_launcher_round.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-mdpi\ic_launcher_round.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xhdpi\ic_launcher_round.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xxhdpi\ic_launcher_round.png
  echo ^<!-- Placeholder, replace with real PNG icon file --^> > app\src\main\res\mipmap-xxxhdpi\ic_launcher_round.png
  
  echo.
  echo WARNING: These are only placeholder files, not actual icon images
  echo It's recommended to use Android Studio's Image Asset Studio to generate real icons
) else (
  echo Invalid option, please run the script again and select 1 or 2
  goto :error
)

echo.
echo Cleaning up any conflicting icon files...
call clean_icons.bat

echo.
echo ===== Operation Complete! =====
goto :end

:error
echo.
echo ===== Operation Failed! =====
exit /b 1

:end
echo.
echo Press any key to exit...
pause > nul 
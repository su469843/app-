@echo off
chcp 65001>nul
echo ===== Cleaning Conflicting Icon Files =====
echo.

echo Checking for icon conflicts...

set ICON_DIRS=app\src\main\res\mipmap-hdpi app\src\main\res\mipmap-mdpi app\src\main\res\mipmap-xhdpi app\src\main\res\mipmap-xxhdpi app\src\main\res\mipmap-xxxhdpi

for %%d in (%ICON_DIRS%) do (
  if exist "%%d" (
    echo Checking directory: %%d
    
    rem Check ic_launcher files
    if exist "%%d\ic_launcher.webp" (
      if exist "%%d\ic_launcher.png" (
        echo Found conflict: %%d\ic_launcher.webp and %%d\ic_launcher.png
        echo Removing: %%d\ic_launcher.webp
        del "%%d\ic_launcher.webp"
      )
    )
    
    rem Check ic_launcher_round files
    if exist "%%d\ic_launcher_round.webp" (
      if exist "%%d\ic_launcher_round.png" (
        echo Found conflict: %%d\ic_launcher_round.webp and %%d\ic_launcher_round.png
        echo Removing: %%d\ic_launcher_round.webp
        del "%%d\ic_launcher_round.webp"
      )
    )
  )
)

echo.
echo ===== Cleaning Complete =====
echo All conflicting icon files have been resolved.
echo.
echo Press any key to exit...
pause > nul 
@echo off
chcp 65001>nul
echo ===== Android App Signing Keystore Generator =====
echo.

set /p ALIAS=Enter key alias name: 
set /p KEYSTORE_PASSWORD=Enter keystore password: 
set /p KEY_PASSWORD=Enter key password (press Enter to use same as keystore): 

if "%KEY_PASSWORD%"=="" set KEY_PASSWORD=%KEYSTORE_PASSWORD%

echo.
echo Generating keystore file...
echo This may take a moment...

if not exist keys mkdir keys

keytool -genkeypair -v -keystore keys/app_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias %ALIAS% -storepass %KEYSTORE_PASSWORD% -keypass %KEY_PASSWORD% -dname "CN=Weather App,OU=Development,O=Your Organization,L=City,ST=State,C=Country"

echo.
echo Keystore generated at: %CD%\keys\app_keystore.jks
echo.
echo For GitHub Actions setup, you'll need to:
echo 1. Base64 encode your keystore:
echo    certutil -encode keys\app_keystore.jks keys\keystore.base64
echo    type keys\keystore.base64 | findstr /v /c:- /c:BEGIN /c:END > keys\keystore_clean.base64
echo.
echo 2. Set up these GitHub secrets:
echo    KEYSTORE_BASE64: The contents of keystore_clean.base64
echo    KEYSTORE_PASSWORD: %KEYSTORE_PASSWORD%
echo    KEYSTORE_ALIAS: %ALIAS%
echo    KEY_PASSWORD: %KEY_PASSWORD%
echo.
echo Press any key to exit...
pause > nul 
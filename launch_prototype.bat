start http://127.0.0.1:8080
cassini\UltiDevCassinWebServer2a.exe /run "%~dp0prototype" "index.html" "8080" "nobrowser"
taskkill /IM UltiDevCassinWebServer2a.exe /F
echo Bye!
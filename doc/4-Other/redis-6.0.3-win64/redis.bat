taskkill /f /fi "windowtitle eq redis≤‚ ‘"
cd %~dp0
del /Q dump.rdb
start "redis≤‚ ‘" redis-server.exe
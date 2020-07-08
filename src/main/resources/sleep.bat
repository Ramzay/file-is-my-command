@echo off
taskkill /F /IM vlc.exe /T
taskkill /F /IM chrome.exe /T
taskkill /F /IM firefox.exe /T
taskkill /F /IM discord.exe /T
taskkill /F /IM notepad.exe /T
taskkill /F /IM steam.exe /T
REM taskkill /F /IM notepad++.exe /T
ping -n 4 127.0.0.1 > nul
REM shutdown.exe /h
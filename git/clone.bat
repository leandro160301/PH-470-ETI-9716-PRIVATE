@echo off
cd ..
set /p REPO_URL=<url.txt

git clone %REPO_URL%

echo Proyecto clonado desde %REPO_URL%
pause
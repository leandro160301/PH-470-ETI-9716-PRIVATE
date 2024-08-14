@echo off
cd ..
set /p REPO_URL=<url.txt

git init
git remote add origin %REPO_URL%
git pull origin master

echo Cambios obtenidos desde %REPO_URL%
pause
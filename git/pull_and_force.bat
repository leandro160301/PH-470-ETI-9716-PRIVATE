@echo off
cd ..
set /p REPO_URL=<url.txt

git init
git remote set-url origin %REPO_URL%
git pull origin master --force

echo Cambios obtenidos desde %REPO_URL%
pause
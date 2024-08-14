@echo off
cd ..
set /p REPO_URL=<url.txt

git init
git add .
git commit -m "Commit automático"
git remote rm origin
git remote add origin %REPO_URL%
git push -u origin master --force

echo Cambios subidos a %REPO_URL%
pause
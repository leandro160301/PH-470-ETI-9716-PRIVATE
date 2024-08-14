@echo off
cd ..
set /p REPO_URL=<url.txt

git init
git remote set-url origin %REPO_URL%
git add .
git commit -m "commit inicial"
git push -u origin master

echo Repositorio inicializado y cambios subidos a %REPO_URL%
pause
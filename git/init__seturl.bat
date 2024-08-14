@echo off
cd ..
set /p REPO_URL=<url.txt

git init
git remote set-url origin %REPO_URL%

echo URL Repositorio actualizado %REPO_URL%
pause
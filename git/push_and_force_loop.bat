@echo off

rem Almacenar el directorio original
set ORIGINAL_DIR=%cd%

rem Solicitar la URL del repositorio remoto una vez
set /p REPO_URL=<url.txt

:loop
rem Cambiar al directorio padre
cd ..

rem Inicializar el repositorio Git en el directorio actual (que ahora es el directorio padre)
git init
git add .
git commit -m "Commit automático"
git remote add origin %REPO_URL%
git push -u origin master --force

echo Cambios subidos a %REPO_URL%
echo.
echo Presiona cualquier tecla para repetir, o Ctrl+C para salir.
pause >nul

rem Regresar al directorio original
cd %ORIGINAL_DIR%

rem Repetir el bucle
goto loop
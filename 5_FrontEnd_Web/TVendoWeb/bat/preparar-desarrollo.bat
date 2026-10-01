@echo off
setlocal
chcp 65001 >nul

set "PROJECT_ROOT=%~dp0.."
pushd "%PROJECT_ROOT%" || goto :root_error

echo ========================================
echo TVendoWeb - Preparar desarrollo local
echo ========================================
echo.

where node >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Node.js no esta disponible en PATH.
  goto :error
)

where npm >nul 2>nul
if errorlevel 1 (
  echo [ERROR] npm no esta disponible en PATH.
  goto :error
)

if not exist "package-lock.json" (
  echo [ERROR] No se encontro package-lock.json.
  goto :error
)

echo [1/2] Instalando dependencias exactas y generando node_modules...
call npm ci
if errorlevel 1 goto :error

echo.
echo [2/2] Compilando con la configuracion de desarrollo...
call npm run build -- --configuration development
if errorlevel 1 goto :error

echo.
echo Preparacion de desarrollo finalizada correctamente.
echo Configuracion usada: src\environments\environment.ts
popd
exit /b 0

:error
echo.
echo [ERROR] No se pudo preparar el entorno de desarrollo.
popd
exit /b 1

:root_error
echo [ERROR] No se pudo acceder a la raiz del proyecto.
exit /b 1

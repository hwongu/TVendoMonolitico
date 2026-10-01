@echo off
setlocal
chcp 65001 >nul

set "PROJECT_ROOT=%~dp0.."
pushd "%PROJECT_ROOT%" || goto :root_error

echo ========================================
echo TVendoWeb - Generar publicacion
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

if not exist "src\environments\environment.production.ts" (
  echo [ERROR] No se encontro environment.production.ts.
  goto :error
)

echo [1/2] Instalando dependencias exactas desde package-lock.json...
call npm ci
if errorlevel 1 goto :error

echo.
echo [2/2] Generando la aplicacion para produccion...
call npm run build -- --configuration production
if errorlevel 1 goto :error

echo.
echo Aplicacion de produccion generada correctamente.
echo Configuracion usada: src\environments\environment.production.ts
echo Salida: dist\tvendo-web
popd
exit /b 0

:error
echo.
echo [ERROR] No se pudo generar la aplicacion de produccion.
popd
exit /b 1

:root_error
echo [ERROR] No se pudo acceder a la raiz del proyecto.
exit /b 1

@echo off
setlocal
chcp 65001 >nul

set "PROJECT_ROOT=%~dp0.."
pushd "%PROJECT_ROOT%" || goto :root_error

echo ========================================
echo TVendoWeb - Limpieza para Git
echo ========================================
echo.
echo Se eliminaran estas carpetas regenerables si existen:
echo   - node_modules
echo   - dist
echo   - .angular\cache
echo   - coverage
echo.
echo No se eliminara codigo fuente ni archivos de configuracion.
echo.

choice /C SN /N /M "Desea continuar? [S/N]: "
if errorlevel 2 goto :cancelled

call :remove_directory "node_modules"
if errorlevel 1 goto :cleanup_error

call :remove_directory "dist"
if errorlevel 1 goto :cleanup_error

call :remove_directory ".angular\cache"
if errorlevel 1 goto :cleanup_error

call :remove_directory "coverage"
if errorlevel 1 goto :cleanup_error

echo.
echo Limpieza finalizada correctamente.
popd
exit /b 0

:remove_directory
if not exist "%~1" (
  echo [OMITIDO] %~1 no existe.
  exit /b 0
)

echo [ELIMINANDO] %~1
rmdir /S /Q "%~1"
if exist "%~1" (
  echo [ERROR] No se pudo eliminar %~1.
  exit /b 1
)
echo [OK] %~1 eliminado.
exit /b 0

:cancelled
echo.
echo Operacion cancelada. No se elimino ninguna carpeta.
popd
exit /b 0

:cleanup_error
echo.
echo La limpieza no pudo completarse.
popd
exit /b 1

:root_error
echo [ERROR] No se pudo acceder a la raiz del proyecto.
exit /b 1

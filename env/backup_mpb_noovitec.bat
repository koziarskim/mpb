@echo off
rem Make sure to set PGPASSWORD environment variable.  Just like "JAVA_HOME".

for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (set datestr=%%l_%%j_%%k)
rem for /f "tokens=1-4 delims=/:." %%a in ("%TIME%") do (set timestr=%%a_%%b_%%c)

set ARG=%1%
set EXPORT=false
set IMPORT=false
set DATE_FORMAT=%datestr%
set EXPORT_DIR=C:\Users\kozia\postgres\backups\noovitec
set SCHEMA_FILE_PUBLIC=%EXPORT_DIR%\schema_public_%DATE_FORMAT%.sql
set DATA_FILE_PUBLIC=%EXPORT_DIR%\data_public_%DATE_FORMAT%.sql
set DB_NAME=mpb_%DATE_FORMAT%
echo Schemas: %SCHEMA_FILE_PUBLIC%
echo Data: %DATA_FILE_PUBLIC%
echo DB name: %DB_NAME%

if "%ARG%"=="" (
	set EXPORT=true
)
if "%ARG%"=="export" (
	set EXPORT=true
)
if "%ARG%"=="import" (
	set IMPORT=true
)
if "%ARG%"=="full" (
	set EXPORT=true
	set IMPORT=true
)
:export
if "%EXPORT%" == "true" (
	echo Processing Export...
	if exist %SCHEMA_FILE_PUBLIC% (echo Backup Failed! Schema File already exist %SCHEMA_FILE_PUBLIC% && GOTO end)
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --file %SCHEMA_FILE_PUBLIC% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --schema-only --schema "public" "mpb"
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --exclude-table-data "doc_content" --file %DATA_FILE_PUBLIC% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --data-only --schema "public" "mpb"
)
:import
if "%IMPORT%" == "true" (
	echo Processing Import...
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %SCHEMA_FILE_PUBLIC% > %EXPORT_DIR%\clean_schema_public.sql
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %DATA_FILE_PUBLIC% > %EXPORT_DIR%\clean_data_public.sql
	createdb -h localhost -p 5432 -U postgres %DB_NAME% || (echo Backup Failed DB already exist && GOTO end)
	psql -U postgres -d %DB_NAME% < %EXPORT_DIR%\clean_schema_public.sql
	psql -U postgres -d %DB_NAME% < %EXPORT_DIR%\clean_data_public.sql
)
:end
echo Done!
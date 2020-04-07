@echo off
rem Make sure to set PGPASSWORD environment variable.  Just like "JAVA_HOME".

for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (set datestr=%%l_%%j_%%k)
rem for /f "tokens=1-4 delims=/:." %%a in ("%TIME%") do (set timestr=%%a_%%b_%%c)

set ARG=%1%
set BACKUP=false
set IMPORT=false
set DATE_FORMAT=%datestr%
set BACKUP_DIR=C:\Users\kozia\postgres\backups\noovitec
set SCHEMA_FILE=%BACKUP_DIR%\backup_year_schema_%DATE_FORMAT%.sql
set DATA_FILE=%BACKUP_DIR%\backup_year_data_%DATE_FORMAT%.sql
set DB_NAME=mpb_%DATE_FORMAT%
echo Schema file name is %SCHEMA_FILE%
echo Data file name is %DATA_FILE%
echo DB name: %DB_NAME%

if "%ARG%"=="" (
	set BACKUP=true
)
if "%ARG%"=="backup" (
	set BACKUP=true
)
if "%ARG%"=="import" (
	set IMPORT=true
)
if "%ARG%"=="full" (
	set BACKUP=true
	set IMPORT=true
)
:backup
if "%BACKUP%" == "true" (
	echo Processing Backup...
	if exist %SCHEMA_FILE% (echo Schema File already exist %SCHEMA_FILE% && GOTO import)
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --file %SCHEMA_FILE% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --schema-only --schema "y2019" "mpb"
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --exclude-table-data "doc_content" --file %DATA_FILE% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --data-only --schema "y2019" "mpb"
)
:import
if "%IMPORT%" == "true" (
	echo Processing Import...
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %SCHEMA_FILE% > %BACKUP_DIR%\clean_schema.sql
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %DATA_FILE% > %BACKUP_DIR%\clean_data.sql
	createdb -h localhost -p 5432 -U postgres %DB_NAME% || (GOTO end)
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_schema.sql
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_data.sql
)
:end
echo Done!
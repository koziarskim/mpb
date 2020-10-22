@echo off
rem Make sure to set PGPASSWORD environment variable.  Just like "JAVA_HOME".

for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (set datestr=%%l_%%j_%%k)
rem for /f "tokens=1-4 delims=/:." %%a in ("%TIME%") do (set timestr=%%a_%%b_%%c)

set ARG=%1%
set BACKUP=false
set IMPORT=false
set DATE_FORMAT=%datestr%
set BACKUP_DIR=C:\Users\kozia\postgres\backups\noovitec
set SCHEMA_FILE_SHARED=%BACKUP_DIR%\schema_shared_%DATE_FORMAT%.sql
set SCHEMA_FILE_2020=%BACKUP_DIR%\schema_2020_%DATE_FORMAT%.sql
set DATA_FILE_SHARED=%BACKUP_DIR%\data_shared_%DATE_FORMAT%.sql
set DATA_FILE_2020=%BACKUP_DIR%\data_2020_%DATE_FORMAT%.sql
set DB_NAME=mpb_%DATE_FORMAT%
echo Schemas: %SCHEMA_FILE_SHARED% , %SCHEMA_FILE_2019% , %SCHEMA_FILE_2020%
echo Data: %DATA_FILE_SHARED% , %DATA_FILE_2019% , %DATA_FILE_2020%
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
	if exist %SCHEMA_FILE_SHARED% (echo Schema File already exist %SCHEMA_FILE_SHARED% && GOTO import)
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --file %SCHEMA_FILE_SHARED% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --schema-only --schema "shared" "mpb"
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --file %SCHEMA_FILE_2020% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --schema-only --schema "y2020" "mpb"
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --exclude-table-data "doc_content" --file %DATA_FILE_SHARED% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --data-only --schema "shared" "mpb"
		"C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --exclude-table-data "doc_content" --file %DATA_FILE_2020% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --data-only --schema "y2020" "mpb"
)
:import
if "%IMPORT%" == "true" (
	echo Processing Import...
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %SCHEMA_FILE_SHARED% > %BACKUP_DIR%\clean_schema_shared.sql
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %SCHEMA_FILE_2020% > %BACKUP_DIR%\clean_schema_2020.sql
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %DATA_FILE_SHARED% > %BACKUP_DIR%\clean_data_shared.sql
	findstr /v /b /c:"SET " /c:"CREATE DATABASE" /c:"ALTER DATABASE" /c:"\connect" /c:"REVOKE ALL" /c:"GRANT ALL" /c:"SELECT pg_catalog.set_config('search_path'" %DATA_FILE_2020% > %BACKUP_DIR%\clean_data_2020.sql
	createdb -h localhost -p 5432 -U postgres %DB_NAME% || (GOTO end)
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_schema_shared.sql
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_schema_2020.sql
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_data_shared.sql
	psql -U postgres -d %DB_NAME% < %BACKUP_DIR%\clean_data_2020.sql
)
:end
echo Done!
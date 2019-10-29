@echo off
rem Make sure to set PGPASSWORD environment variable.  Just like "JAVA_HOME".

for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (
 set dow=%%i
 set month=%%j
 set day=%%k
 set year=%%l
)
set datestr=%month%_%day%_%year%
echo datestr is %datestr%

set SCHEMA_FILE=C:\Users\kozia\postgres\backups\noovitec\backup_schema_%datestr%.sql
set DATA_FILE=C:\Users\kozia\postgres\backups\noovitec\backup_data_%datestr%.sql
echo backup schema file name is %SCHEMA_FILE%
echo backup data file name is %DATA_FILE%
echo on
rem bin\pg_dump -h <HostName> -p 5432 -U <UserName> -F c -b -v -f %BACKUP_FILE% <DATABASENAME>
rem "C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_restore.exe" --file %SCHEMA_FILE% --host "localhost" --port "5432" --username "postgres" --verbose "mpb"

"C:\Program Files (x86)\pgAdmin 4\v4\runtime\psql.exe" -h localhost -p 5432 -U postgres -d mpb < %SCHEMA_FILE%

rem "C:\Program Files (x86)\pgAdmin 4\v4\runtime\pg_dump.exe" --file %DATA_FILE% --host "mpb.noovitec.com" --port "5432" --username "postgres" --verbose --format=p --create --inserts --column-inserts --data-only "mpb"
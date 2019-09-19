@echo off
SETLOCAL

set import=true
set host=localhost
set port=5432
set user=postgres
set dbname=mpb2
set PGPASSWORD=s3cret

:initial
if "%1"=="" goto done
set aux=%1
if "%aux:~0,1%"=="-" (
   set nome=%aux:~1,250%
) else (
   set "%nome%=%1"
   set nome=
)
shift
goto initial
:done

::echo %import%

if "%import%" == "true" (
	echo Running psql import_main.sql
	rem dropdb --if-exists -h %host% -p %port% -U %user% -w %dbname%
	rem createdb -h %host% -p %port% -U %user% -w %dbname%
	rem psql -h %host% -p %port% -U %user% -d %dbname% --single-transaction --file=schema.sql
	psql -h %host% -p %port% -U %user% -d %dbname% --single-transaction --file=data.sql
)

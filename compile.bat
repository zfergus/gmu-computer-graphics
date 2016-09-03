@echo off

REM Need JOGL directory at C:/jogl-1.1.1-windows-amd64
set jogl_path=./jogl-1.1.1-windows-amd64

REM Checks to see if the jogl_path exists and that it has a lib sub-directory.
:NO_JOGL_DIR
if exist %jogl_path% if exist %jogl_path%/lib goto PATH_SET
echo The JOGL directory, %jogl_path%, either does not exist or does not contain^
 a lib sub-folder for the JOGL library.
set /p jogl_path=Please enter the path of the base JOGL directory:
goto NO_JOGL_DIR

:PATH_SET
SET class_path=.;./bin;%jogl_path%/lib/gluegen-rt.jar;%jogl_path%/lib/jogl.jar

if exist .\bin goto COMPILE
mkdir .\bin

:COMPILE
Echo Compiling
javac -cp %class_path% -d .\bin -g *.java

Echo Done
Echo.
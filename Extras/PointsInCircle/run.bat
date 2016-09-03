@echo off

REM Check that the compile script is available.
if not exist compile.bat goto COMPILE
call compile.bat
goto RUN

:COMPILE
REM Need JOGL directory at C:/jogl-1.1.1-windows-amd64
set jogl_path=c:/jogl-1.1.1-windows-amd64

REM Checks to see if the jogl_path exists and that it has a lib sub-directory.
:NO_JOGL_DIR
if exist %jogl_path% if exist %jogl_path%/lib goto PATH_SET
echo The JOGL directory, %jogl_path%, either does not exist or does not contain^
 a lib sub-folder for the JOGL library.
set /p jogl_path=Please enter the path of the base JOGL directory:
goto NO_JOGL_DIR

:PATH_SET
SET class_path=.;%jogl_path%/lib/gluegen-rt.jar;%jogl_path%/lib/jogl.jar

Echo Compiling
javac -cp %class_path% *.java

Echo Done

:RUN
echo Running
REM java -cp %class_path% -Djava.library.path=%jogl_path%/lib J1_0_Point
REM java -cp %class_path% -Djava.library.path=%jogl_path%/lib J1_1_Point

set /p num_points=Enter the number of points:
java -cp %class_path% -Djava.library.path=%jogl_path%/lib PointsInCircle^
 %num_points%

echo Done
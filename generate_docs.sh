#!/bin/bash

# Need JOGL directory at ./jogl-1.1.1-linux-amd64
jogl_path="./jogl-1.1.1-linux-amd64"

# Checks to see if the jogl_path exists and that it has a lib sub-directory.
while ! [ -d "$jogl_path" ] && ! [ -d "$jogl_path/lib" ]
do
printf "The JOGL directory, %s, either does not exist or does not contain a lib sub-folder for the JOGL library.\n" "$jogl_path"
echo -n "Please enter the path of the base JOGL directory:"
read jogl_path
done

class_path=".:./bin:$jogl_path/lib/gluegen-rt.jar:$jogl_path/lib/jogl.jar"

printf "Generating Documentation\n"
javadoc -sourcepath $class_path -cp $class_path -d ./docs *.java -private -author

printf "Done\n"

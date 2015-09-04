@REM test1
javac test1\com\codemacro\test\*.java -d classes\test1 -cp test1\;test2\
del classes\test1\com\codemacro\test\Export.class
mkdir ..\bundle\test1\
mkdir ..\bundle\test1\classes
copy test1.prop ..\bundle\test1\ /Y
xcopy classes\test1 ..\bundle\test1\classes\ /S /Y

@REM test2
javac test2\com\codemacro\test\*.java -d classes\test2 -cp test1\;test2\;..\kcontainer.interface\src\main\java\
del classes\test2\com\codemacro\container\* /Q
del classes\test2\com\codemacro\test\A.class /Q
jar cf test2.jar -C classes\test2 com
mkdir ..\bundle\test2\
mkdir ..\bundle\test2\lib\
copy test2.prop ..\bundle\test2\ /Y
copy test2.jar ..\bundle\test2\lib\ /Y

@REM to kcontainer\bundle, test in eclipse
mkdir ..\kcontainer\bundle\test1
xcopy ..\bundle\test1 ..\kcontainer\bundle\test1\ /S /Y
mkdir ..\kcontainer\bundle\test2
xcopy ..\bundle\test2 ..\kcontainer\bundle\test2\ /S /Y

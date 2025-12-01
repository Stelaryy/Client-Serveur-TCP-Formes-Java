:: bat
::   Compilation/Execution 	Serveur_Java_TCP_Socket : Serveur TCP, Serveur TCP, utilisant les sockets en java
::
:: @author Wilfrid Grassi : 26/11/2017  v1.0
::

set NOM_DU_PROGRAMME=Serveur_Java_TCP_Socket


echo off
cls
echo.
echo.
del bin\*.class /Y
cls
echo Compilation Java de %NOM_DU_PROGRAMME% en cours....
cd src
javac -d "..\bin" %NOM_DU_PROGRAMME%.java
echo Compilation terminee.
echo.
cd ../bin
echo Execution de %NOM_DU_PROGRAMME% en cours....
echo.
java %NOM_DU_PROGRAMME%
cd ..
pause
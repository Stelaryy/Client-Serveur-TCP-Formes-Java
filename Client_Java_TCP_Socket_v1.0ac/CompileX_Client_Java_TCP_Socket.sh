#!/bin/sh
#   Compilation/Execution du jar executable Client_Java_TCP_Socket : Client TCP, utilisant les sockets en java
#
# @author Wilfrid Grassi : 26/11/2017  v1.0
#

#----------------------------------- !!!! LA SEULE VARIABLE A MODIFIER !!!!! ------------------------------------------
NOM_DU_PROGRAMME='Client_Java_TCP_Socket'
#----------------------------------- !!!! LA SEULE VARIABLE A MODIFIER !!!!! ------------------------------------------

while true;do
	#Compilation et Execution du programme Client_Java_TCP_Socket
	echo
	echo
	echo "Compilation Java de $NOM_DU_PROGRAMME en cours...."
	rm -f bin/*.class
	cd src
	sudo javac -d "../bin" $NOM_DU_PROGRAMME.java
	echo "Compilation terminee."
	echo
	cd ../bin
	echo "Execution de $NOM_DU_PROGRAMME en cours...."
	echo
	sudo java $NOM_DU_PROGRAMME
	cd ..
	setterm -cursor on
	reset
done

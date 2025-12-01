#!/bin/sh
#   Execution 	Serveur_Java_TCP_Socket: Serveur TCP, utilisant les sockets en java
#
# @author Wilfrid Grassi : 26/11/2017  v1.0
#

NOM_DU_PROGRAMME='Serveur_Java_TCP_Socket'

reset
	
while true;do
	echo
	echo
	cd bin
	echo "Execution de $NOM_DU_PROGRAMME en cours...."
	echo
	sudo java $NOM_DU_PROGRAMME
	cd ..
	setterm -cursor on
	reset

	touche=""
done

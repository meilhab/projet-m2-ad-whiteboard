#!/bin/bash
if [ $# != 2 ] ; then
    echo "Usage: sh $0 typeProtocole portGroupe"
    exit
fi

gnome-terminal -t "Groupe" --geometry=60x20 -e "bash -c 'java -cp whiteboard.jar lanceur.LanceurGroupe ${2}; exec bash'" &
sleep 1

for i in 0 1 2 3 4
do
    gnome-terminal -t "Client [$i]" --geometry=60x20 -e "bash -c 'java -cp whiteboard.jar lanceur.LanceurClient ${1} ${2}; exec bash'" &
    sleep 1
done

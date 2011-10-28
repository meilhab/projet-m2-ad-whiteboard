#!/bin/bash
if [ $# != 1 ] ; then
    echo "Usage: sh $0 typeProtocole"
    exit
fi

gnome-terminal -t "Groupe" -e "bash -c 'java -cp whiteboard.jar lanceur.LanceurGroupe; exec bash'" &
sleep 1

for i in {0..4}
do
    gnome-terminal -t "Client [$i]" -e "bash -c 'java -cp whiteboard.jar lanceur.LanceurClient ${1}; exec bash'" &
    sleep 1
done

if [ $# != 3 ]; then
    echo "Usage: sh ${0} typeProtocole(Lamport/SuzukiKasami/NaimiTrehel)
        ipGroupe portGroupe"
    exit
fi

java -cp whiteboard.jar lanceur.LanceurClient ${1} ${2} ${3}

if [ $# != 1 ]; then
    echo "Usage: sh ${0} portGroupe"
    exit
fi

java -cp whiteboard.jar lanceur.LanceurGroupe ${1}

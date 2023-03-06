# msrp-api-retailer

Pour lancer l'application, il faudra avant tout docker, ainsi que les fichiers suivants : 
-secret.properties (a mettre dans le dossier resources du projet)
-.env (a mettre dans le root du projet)

Pour lancer l'application, lancer la commande docker suivante depuis le root du projet : 

docker-compose up -d

Lorsque le projet sera lancé, vous pourrez effectuer vos appels à l'API.

Dans la collection postman vous trouverez les differents endpoints disponibles.

pour l'enregistrement : 

Fournir toutes les informations est obligatoire, 
il faudra ensuite regarder dans le server mail (le port 1080) le mail envoyé contenant le token.

le parametre a mettre en header pour la clé est APIKEY

# ProjetPoo4A


Nous avons décidé de faire un projet maven.


Nos différentes classes se trouvent dans maven_project\chat\src\main.

Interface utilisateur :
  fenêtre de connexion : ok
  Choix de l'utilsateur avec qui discuter : ok
  fenêtre de chat : ok 
  
 Pour lancer : dans un terminal lancer jar.App 25555 127.0.0.1
 Dans un autre terminal : lancer jar.App 25556 127.0.0.1
 ..... rajouter +1 au port à chaque nouvel agent.
  
  
Historique : nous avons choisi d'utiliser sqlite.



Deuxième version avec ip:
Plus besoin d'argument : lancer jar.App
Il faut au préalable rajouter une règle dans le pare-feu pour autoriser les traffic entrant et sortant java.


Une fenêtretre s'ouvre, il faut alors entrer son pseudo puis cliquer sur connexion, s'affiche alors dans une liste déroulante tous les utilisateurs disponibles.
Choisir un pseudo puis cliquer sur discuter avec  -> une fenêtre de chat s'ouvre, l'historique des conversations précedentes avec l'autre utilisateur s'affiche.


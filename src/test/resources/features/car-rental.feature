# language: fr
Fonctionnalité: Gestion des locations de voitures

  Scénario: Lister toutes les voitures disponibles
    Étant donné des voitures sont disponibles
    Quand je demande la liste des voitures
    Alors toutes les voitures sont affichées

  Scénario: Louer une voiture avec succès
    Étant donné une voiture est disponible
    Quand je loue cette voiture
    Alors la voiture n'est plus disponible

  Scénario: Retourner une voiture
    Étant donné une voiture est louée
    Quand je retourne cette voiture
    Alors la voiture est marquée comme disponible
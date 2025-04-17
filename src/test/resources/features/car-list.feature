@car-list
Feature: Affichage des voitures disponibles
  En tant qu'utilisateur
  Je veux voir la liste des voitures disponibles
  Afin de choisir une voiture à louer

  @happy-path
  Scenario: Lister toutes les voitures disponibles
    Given des voitures sont disponibles dans le système
      | marque   | modèle      | année | prixJournalier | disponible |
      | Renault  | Clio        | 2020  | 45             | true       |
      | Peugeot  | 208         | 2021  | 50             | true       |
      | Citroën  | C3          | 2019  | 40             | false      |
    
    When je fais une requête GET sur '/api/cars/available'
    
    Then la réponse doit avoir un statut 200
    And la réponse doit contenir 2 voitures
    And la réponse doit inclure une 'Renault Clio 2020'
    And la réponse ne doit pas inclure de 'Citroën C3 2019'
    
    Examples:
      | expectedCount |
      | 2             |

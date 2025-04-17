@car-rental
Feature: Location de voiture
  En tant que client
  Je veux pouvoir louer une voiture
  Afin de l'utiliser pour mes déplacements

  @success @transaction
  Scenario: Louer une voiture avec succès
    Given une voiture est disponible
      """
      {
        "id": 1,
        "marque": "Toyota",
        "modèle": "Corolla",
        "année": 2022,
        "disponible": true
      }
      """
    And un client existe avec l'ID 100
    
    When je fais une requête POST sur '/api/rentals' avec le corps
      """
      {
        "carId": 1,
        "clientId": 100,
        "startDate": "2023-06-15",
        "endDate": "2023-06-20"
      }
      """
    
    Then la réponse doit avoir un statut 201
    And la voiture avec l'ID 1 doit être marquée comme non disponible
    And le contrat de location doit être enregistré dans le système
    
    Examples:
      | carStatus | rentalRecordCount |
      | false     | 1                 |

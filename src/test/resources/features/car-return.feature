@car-return
Feature: Retour de voiture
  En tant que client
  Je veux pouvoir retourner une voiture louée
  Afin de terminer ma location

  @success @transaction
  Scenario: Retourner une voiture louée
    Given une location est en cours avec les détails
      | carId | clientId | startDate  | endDate    |
      | 5     | 200      | 2023-06-10 | 2023-06-17 |
    And la voiture avec l'ID 5 est marquée comme non disponible
    
    When je fais une requête PUT sur '/api/rentals/5/return' avec le corps
      """
      {
        "returnDate": "2023-06-16",
        "condition": "BONNE",
        "additionalCharges": 0
      }
      """
    
    Then la réponse doit avoir un statut 200
    And la voiture avec l'ID 5 doit être marquée comme disponible
    And la location doit être marquée comme complétée
    And aucun dommage ne doit être signalé
    
    Examples:
      | expectedStatus | damageReported |
      | "COMPLETED"    | false          |
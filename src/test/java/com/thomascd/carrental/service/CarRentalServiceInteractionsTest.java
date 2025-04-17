package com.thomascd.carrental.service;

import org.junit.jupiter.api.Test; // (JUnit 5)
import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import com.thomascd.carrental.model.Car;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CarRentalServiceInteractionTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    private final String REG_NUMBER = "ABC123";
    private final Car testCar = new Car(REG_NUMBER, "Toyota", true);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test d'interaction entre rentCar -> find -> update
    @Test
    void rentCar_shouldCallFindAndUpdateInSequence_WhenCarIsAvailable() {
        // Configuration du mock
        when(carRepository.findByRegistrationNumber(REG_NUMBER))
            .thenReturn(Optional.of(testCar));
        
        // Action
        boolean result = carRentalService.rentCar(REG_NUMBER);

        // Vérification des interactions
        assertTrue(result);
        
        // Vérifie que findByRegistrationNumber a été appelé exactement une fois
        verify(carRepository, times(1)).findByRegistrationNumber(REG_NUMBER);
        
        // Vérifie que updateCar a été appelé exactement une fois avec la voiture modifiée
        verify(carRepository, times(1)).updateCar(argThat(car -> 
            car.getRegistrationNumber().equals(REG_NUMBER) && !car.isAvailable()
        ));
        
        // Vérifie l'ordre des appels: find doit être appelé avant update
        inOrder(carRepository).verify(carRepository).findByRegistrationNumber(REG_NUMBER);
        inOrder(carRepository).verify(carRepository).updateCar(any(Car.class));
    }

    // Test d'interaction entre getAllCars -> carRepository.getAllCars
    @Test
    void getAllCars_shouldDelegateToRepository() {
        // Configuration
        List<Car> expectedCars = Collections.singletonList(testCar);
        when(carRepository.getAllCars()).thenReturn(expectedCars);

        // Action
        List<Car> result = carRentalService.getAllCars();

        // Vérification
        assertEquals(expectedCars, result);
        verify(carRepository, times(1)).getAllCars();
        verifyNoMoreInteractions(carRepository); // Aucune autre méthode ne doit être appelée
    }

    // Test d'interaction pour returnCar avec car non trouvé
    @Test
    void returnCar_shouldOnlyCallFind_WhenCarNotFound() {
        // Configuration
        when(carRepository.findByRegistrationNumber(REG_NUMBER))
            .thenReturn(Optional.empty());

        // Action
        carRentalService.returnCar(REG_NUMBER);

        // Vérification
        verify(carRepository, times(1)).findByRegistrationNumber(REG_NUMBER);
        verify(carRepository, never()).updateCar(any());
    }

    // Test d'interaction complet pour returnCar
    @Test
    void returnCar_shouldCallFindThenUpdate_WhenCarExists() {
        // Configuration - voiture non disponible
        Car rentedCar = new Car(REG_NUMBER, "Toyota", false);
        when(carRepository.findByRegistrationNumber(REG_NUMBER))
            .thenReturn(Optional.of(rentedCar));

        // Action
        carRentalService.returnCar(REG_NUMBER);

        // Vérifications
        // 1. Vérifier que find a été appelé
        verify(carRepository, times(1)).findByRegistrationNumber(REG_NUMBER);
        
        // 2. Vérifier que update a été appelé avec une voiture disponible
        verify(carRepository, times(1)).updateCar(argThat(car ->
            car.isAvailable() && car.getRegistrationNumber().equals(REG_NUMBER)
        ));
        
        // 3. Vérifier l'ordre des appels
        inOrder(carRepository).verify(carRepository).findByRegistrationNumber(REG_NUMBER);
        inOrder(carRepository).verify(carRepository).updateCar(rentedCar);
    }

    // Test pour vérifier qu'aucune interaction non désirée ne se produit
    @Test
    void rentCar_shouldNotCallUnnecessaryMethods() {
        when(carRepository.findByRegistrationNumber(REG_NUMBER))
            .thenReturn(Optional.empty());

        boolean result = carRentalService.rentCar(REG_NUMBER);

        assertFalse(result);
        verify(carRepository, never()).getAllCars();
        verify(carRepository, never()).updateCar(any());
        verify(carRepository, only()).findByRegistrationNumber(REG_NUMBER);
    }
}

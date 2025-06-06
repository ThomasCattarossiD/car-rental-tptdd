package com.thomascd.carrental.service;

import org.junit.jupiter.api.Test;
import com.thomascd.carrental.model.Car;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarRentalServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCars_shouldReturnAllCarsFromRepository() {
        // Arrange
        List<Car> expectedCars = Arrays.asList(
            new Car("CAR1", "Model1", true),
            new Car("CAR2", "Model2", false)
        );
        when(carRepository.getAllCars()).thenReturn(expectedCars);

        // Act
        List<Car> actualCars = carRentalService.getAllCars();

        // Assert
        assertEquals(expectedCars, actualCars);
        verify(carRepository, times(1)).getAllCars();
    }

    @Test
    void rentCar_shouldReturnTrueAndUpdateCar_WhenCarIsAvailable() {
        // Arrange
        String regNumber = "AVAIL1";
        Car availableCar = new Car(regNumber, "AvailableModel", true);
        when(carRepository.findByRegistrationNumber(regNumber))
            .thenReturn(Optional.of(availableCar));

        // Act
        boolean result = carRentalService.rentCar(regNumber);

        // Assert
        assertTrue(result);
        assertFalse(availableCar.isAvailable());
        verify(carRepository, times(1)).updateCar(availableCar);
    }

    @Test
    void rentCar_shouldReturnFalse_WhenCarIsNotAvailable() {
        // Arrange
        String regNumber = "UNAVAIL1";
        Car unavailableCar = new Car(regNumber, "UnavailableModel", false);
        when(carRepository.findByRegistrationNumber(regNumber))
            .thenReturn(Optional.of(unavailableCar));

        // Act
        boolean result = carRentalService.rentCar(regNumber);

        // Assert
        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    void rentCar_shouldReturnFalse_WhenCarDoesNotExist() {
        // Arrange
        String regNumber = "NONEXIST";
        when(carRepository.findByRegistrationNumber(regNumber))
            .thenReturn(Optional.empty());

        // Act
        boolean result = carRentalService.rentCar(regNumber);

        // Assert
        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    void returnCar_shouldUpdateCarAvailability_WhenCarExists() {
        // Arrange
        String regNumber = "RETURN1";
        Car carToReturn = new Car(regNumber, "ReturnModel", false);
        when(carRepository.findByRegistrationNumber(regNumber))
            .thenReturn(Optional.of(carToReturn));

        // Act
        carRentalService.returnCar(regNumber);

        // Assert
        assertTrue(carToReturn.isAvailable());
        verify(carRepository, times(1)).updateCar(carToReturn);
    }

    @Test
    void returnCar_shouldDoNothing_WhenCarDoesNotExist() {
        // Arrange
        String regNumber = "NONEXIST";
        when(carRepository.findByRegistrationNumber(regNumber))
            .thenReturn(Optional.empty());

        // Act
        carRentalService.returnCar(regNumber);

        // Assert
        verify(carRepository, never()).updateCar(any());
    }

    // Tests pour addCar (partie 2.1)

    @Test
    void addCar_shouldSuccess_whenRegistrationNumberIsUnique() {
        // Arrange
        Car newCar = new Car("NEW123", "Tesla", true);
        when(carRepository.findByRegistrationNumber("NEW123")).thenReturn(Optional.empty());
        
        // Act
        boolean result = carRentalService.addCar(newCar);
        
        // Assert
        assertTrue(result);
        verify(carRepository).addCar(newCar);
    }

    @Test
    void addCar_shouldFail_whenRegistrationNumberExists() {
        // Arrange
        Car existingCar = new Car("EXIST123", "BMW", true);
        when(carRepository.findByRegistrationNumber("EXIST123"))
            .thenReturn(Optional.of(existingCar));
        
        // Act
        boolean result = carRentalService.addCar(existingCar);
        
        // Assert
        assertFalse(result);
        verify(carRepository, never()).addCar(any());
    }

    // Tests pour searchByModel (partie 2.2)

    @Test
    void searchByModel_shouldReturnMatchingCars() {
        // Arrange
        List<Car> expectedCars = List.of(
            new Car("TES123", "Tesla", true),
            new Car("TES456", "Tesla", false)
        );
        when(carRepository.findByModel("Tesla")).thenReturn(expectedCars);
        
        // Act
        List<Car> result = carRentalService.searchByModel("Tesla");
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getModel().equals("Tesla")));
    }

    @Test
    void searchByModel_shouldReturnEmptyList_whenNoMatches() {
        // Arrange
        when(carRepository.findByModel("Audi")).thenReturn(Collections.emptyList());
        
        // Act
        List<Car> result = carRentalService.searchByModel("Audi");
        
        // Assert
        assertTrue(result.isEmpty());
    }


}

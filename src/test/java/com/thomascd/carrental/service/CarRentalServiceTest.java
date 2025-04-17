package com.thomascd.carrental.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.thomascd.carrental.model.Car;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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
}

package com.thomascd.carrental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thomascd.carrental.model.Car;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {
    
    private CarRepository carRepository;
    
    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void getAllCars_shouldReturnEmptyListInitially() {
        List<Car> cars = carRepository.getAllCars();
        assertTrue(cars.isEmpty());
    }

    @Test
    void addCar_shouldAddCarToRepository() {
        Car car = new Car("ABC123", "Toyota", true);
        carRepository.addCar(car);
        
        List<Car> cars = carRepository.getAllCars();
        assertEquals(1, cars.size());
        assertEquals(car, cars.get(0));
    }

    @Test
    void findByRegistrationNumber_shouldReturnEmptyForNonExistingCar() {
        Optional<Car> result = carRepository.findByRegistrationNumber("NONEXIST");
        assertFalse(result.isPresent());
    }

    @Test
    void findByRegistrationNumber_shouldReturnCarWhenExists() {
        Car car = new Car("XYZ789", "Honda", true);
        carRepository.addCar(car);
        
        Optional<Car> result = carRepository.findByRegistrationNumber("XYZ789");
        assertTrue(result.isPresent());
        assertEquals(car, result.get());
    }

    @Test
    void updateCar_shouldUpdateExistingCar() {
        Car originalCar = new Car("DEF456", "BMW", true);
        carRepository.addCar(originalCar);
        
        Car updatedCar = new Car("DEF456", "BMW", false);
        carRepository.updateCar(updatedCar);
        
        Optional<Car> result = carRepository.findByRegistrationNumber("DEF456");
        assertTrue(result.isPresent());
        assertEquals(updatedCar.isAvailable(), result.get().isAvailable());
    }
}

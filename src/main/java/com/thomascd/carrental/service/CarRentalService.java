package com.thomascd.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thomascd.carrental.model.Car;

import java.util.List;
import java.util.Optional;

@Service
public class CarRentalService {

    @Autowired
    private CarRepository carRepository = new CarRepository();

    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    public boolean rentCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        if (car.isPresent() && car.get().isAvailable()) {
            car.get().setAvailable(false);
            carRepository.updateCar(car.get());
            return true;
        }
        return false;
    }

    public void returnCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        car.ifPresent(c -> {
            c.setAvailable(true);
            carRepository.updateCar(c);
        });
    }

    public boolean addCar(Car car) {
        if (carRepository.findByRegistrationNumber(car.getRegistrationNumber()).isEmpty()) {
            carRepository.addCar(car);
            return true;
        }
        return false;
    }
    
    public List<Car> searchByModel(String model) {
        return carRepository.findByModel(model);
    }
    
    
}
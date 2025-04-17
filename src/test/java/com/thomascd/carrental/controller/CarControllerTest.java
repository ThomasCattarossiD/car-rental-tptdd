package com.thomascd.carrental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomascd.carrental.model.Car;
import com.thomascd.carrental.service.CarRentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRentalService carRentalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Tests pour l'ajout de voiture
    @Test
    void addCar_shouldReturn200_whenSuccess() throws Exception {
        Car newCar = new Car("NEW123", "Tesla", true);
        when(carRentalService.addCar(any(Car.class))).thenReturn(true);

        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(content().string("Voiture ajoutée avec succès"));
    }

    @Test
    void addCar_shouldReturn400_whenDuplicateRegistration() throws Exception {
        Car existingCar = new Car("EXIST123", "BMW", true);
        when(carRentalService.addCar(any(Car.class))).thenReturn(false);

        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingCar)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Le numéro d'immatriculation existe déjà"));
    }

    // Tests pour la recherche par modèle
    @Test
    void searchByModel_shouldReturnCars_whenFound() throws Exception {
        Car car1 = new Car("TES123", "Tesla", true);
        Car car2 = new Car("TES456", "Tesla", false);
        
        when(carRentalService.searchByModel("Tesla"))
            .thenReturn(Arrays.asList(car1, car2));

        mockMvc.perform(get("/cars/search")
                .param("model", "Tesla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("TES123"))
                .andExpect(jsonPath("$[1].registrationNumber").value("TES456"));
    }

    @Test
    void searchByModel_shouldReturnEmptyList_whenNoMatch() throws Exception {
        when(carRentalService.searchByModel("Audi")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cars/search")
                .param("model", "Audi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // Tests pour les fonctionnalités existantes (location/retour)
    @Test
    void rentCar_shouldReturn200_whenSuccess() throws Exception {
        when(carRentalService.rentCar("ABC123")).thenReturn(true);

        mockMvc.perform(post("/cars/rent/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void returnCar_shouldReturn200() throws Exception {
        mockMvc.perform(post("/cars/return/ABC123"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCars_shouldReturn200() throws Exception {
        Car car = new Car("GET123", "Test", true);
        when(carRentalService.getAllCars()).thenReturn(Collections.singletonList(car));

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("GET123"));
    }
}

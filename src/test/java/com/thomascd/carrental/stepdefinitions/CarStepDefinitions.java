package com.thomascd.carrental.stepdefinitions;

import io.cucumber.java.fr.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

import com.thomascd.carrental.model.Car;
import com.thomascd.carrental.service.CarRentalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private CarRentalService carRentalService = new CarRentalService();

    private Response response;
    private String testRegistrationNumber = "TEST-123";

    @Étantdonné("des voitures sont disponibles")
    public void desVoituresSontDisponibles() {
        carRentalService.addCar(new Car(testRegistrationNumber, "Clio", true));
        carRentalService.addCar(new Car("TEST-456", "208", true));
    }

    @Étantdonné("une voiture est disponible")
    public void uneVoitureEstDisponible() {
        carRentalService.addCar(new Car(testRegistrationNumber, "Clio", true));
    }

    @Étantdonné("une voiture est louée")
    public void uneVoitureEstLouee() {
        carRentalService.addCar(new Car(testRegistrationNumber, "Clio", false));
    }

    @Quand("je demande la liste des voitures")
    public void jeDemandeLaListeDesVoitures() {
        response = get("http://localhost:" + port + "/cars");
    }

    @Quand("je loue cette voiture")
    public void jeLoueCetteVoiture() {
        response = post("http://localhost:" + port + "/cars/rent/" + testRegistrationNumber);
    }

    @Quand("je retourne cette voiture")
    public void jeRetourneCetteVoiture() {
        post("http://localhost:" + port + "/cars/return/" + testRegistrationNumber);
    }

    @Alors("toutes les voitures sont affichées")
    public void toutesLesVoituresSontAffichees() {
        response.then().statusCode(200);
        assertTrue(response.jsonPath().getList("").size() >= 2);
    }

    @Alors("la voiture n'est plus disponible")
    public void laVoitureNestPlusDisponible() {
        assertEquals(200, response.statusCode());
        assertEquals(true, response.getBody().as(Boolean.class));
        assertFalse(carRentalService.getAllCars().stream()
            .filter(c -> c.getRegistrationNumber().equals(testRegistrationNumber))
            .findFirst()
            .orElseThrow()
            .isAvailable());
    }

    @Alors("la voiture est marquée comme disponible")
    public void laVoitureEstMarqueeCommeDisponible() {
        assertTrue(carRentalService.getAllCars().stream()
            .filter(c -> c.getRegistrationNumber().equals(testRegistrationNumber))
            .findFirst()
            .orElseThrow()
            .isAvailable());
    }
}
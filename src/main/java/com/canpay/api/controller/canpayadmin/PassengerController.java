package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.Dashboard.Passenger.PassengerDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequestDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.canpay.api.service.dashboard.PassengerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing passengers in the CanPay admin dashboard.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
public class PassengerController {
    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    private final PassengerService passengerService;

    /**
     * Constructor for PassengerController.
     * 
     * @param passengerService the service handling passenger operations
     */
    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    /**
     * Adds a new passenger.
     * 
     * @param request the passenger registration request data
     * @return response entity with operation result
     */
    @PostMapping("/passengers")
    public ResponseEntity<?> addPassenger(@RequestBody @Valid PassengerRegistrationRequestDto request) {
        logger.info("Request received: {}", request);
        UUID passengerId = passengerService.addPassenger(request);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger added successfully")
                .httpStatus(HttpStatus.CREATED)
                .body(Map.of("passengerId", passengerId))
                .buildWrapped();
    }

    /**
     * Retrieves all passengers.
     * 
     * @return response entity with list of all passengers
     */
    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        List<PassengerDto> passengerDtos = passengerService.getAllPassengers();
        return new ResponseEntityBuilder.Builder<List<PassengerDto>>()
                .resultMessage("List of all passengers retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(passengerDtos)
                .buildWrapped();
    }

    /**
     * Retrieves a passenger by their ID.
     * 
     * @param id the UUID of the passenger
     * @return response entity with passenger details
     */
    @GetMapping("/passengers/{id}")
    public ResponseEntity<?> getPassengerById(@PathVariable UUID id) {
        Map<String, Object> response = passengerService.getPassengerById(id);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger details retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(response)
                .buildWrapped();
    }

    /**
     * Edits an existing passenger.
     * 
     * @param id      the UUID of the passenger to edit
     * @param request the updated passenger data
     * @return response entity with operation result
     */
    @PutMapping("/passengers/{id}")
    public ResponseEntity<?> editPassenger(@PathVariable UUID id,
            @RequestBody @Valid PassengerRegistrationRequestDto request) {
        logger.info("Request received: {}", request);
        Map<String, Object> response = passengerService.editPassenger(id, request);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(response)
                .buildWrapped();
    }

    /**
     * Deletes a passenger by their ID.
     * 
     * @param id the UUID of the passenger to delete
     * @return response entity with operation result
     */
    @DeleteMapping("/passengers/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable UUID id) {
        passengerService.deletePassenger(id);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger deleted successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("deleted", true))
                .buildWrapped();
    }

    /**
     * Retrieves the total count of passengers.
     * 
     * @return response entity with passenger count
     */
    @GetMapping("/passengers/count")
    public ResponseEntity<?> getPassengerCount() {
        long count = passengerService.getPassengerCount();
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Total number of passengers retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("passengerCount", count))
                .buildWrapped();
    }
}

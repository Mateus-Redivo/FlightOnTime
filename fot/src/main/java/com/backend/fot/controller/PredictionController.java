package com.backend.fot.controller;

import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.dto.FlightPredictionResponseDTO;
import com.backend.fot.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for flight delay predictions.
 * 
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-17
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Flight Predictions", description = "Endpoints for flight delay prediction using Machine Learning")
public class PredictionController {

    /**
     * Service layer dependency injected via constructor (Dependency Inversion
     * Principle).
     * Using final ensures immutability and thread-safety.
     */
    private final PredictionService predictionService;

    /**
     * 
     * 
     * @param request Flight information (validated automatically by @Valid)
     * @return ResponseEntity with prediction result and HTTP 200 status
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     *                                                         (handled by
     *                                                         GlobalExceptionHandler)
     */
    @Operation(summary = "Predict flight delay", description = """
            Analyzes flight information and returns a prediction indicating whether
            the flight will be ON_TIME or DELAYED, along with the confidence probability.

            The ML model considers factors like:
            - Flight distance
            - Departure time
            - Origin and destination airports
            - Airline company

            Returns a probability score from 0.0 to 1.0 indicating confidence level.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prediction successfully generated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FlightPredictionResponseDTO.class), examples = @ExampleObject(name = "Successful Prediction", value = """
                    {
                      "prediction": "DELAYED",
                      "probability": 0.85,
                      "confidence": "HIGH"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid request data - validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Validation Error", value = """
                    {
                      "timestamp": "2025-12-17T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "errors": [
                        {
                          "field": "flightOrigin",
                          "message": "Airport code must be exactly 3 characters"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "503", description = "ML service temporarily unavailable", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Service Unavailable", value = """
                    {
                      "timestamp": "2025-12-17T14:30:00",
                      "status": 503,
                      "error": "Service Unavailable",
                      "message": "ML prediction service is temporarily unavailable. Please try again later."
                    }
                    """)))
    })
    @PostMapping("/predict")
    public ResponseEntity<FlightPredictionResponseDTO> predictFlightDelay(
            @Valid @RequestBody FlightPredictionRequestDTO request) {

        log.info("Received prediction request for flight {} from {} to {}",
                request.getFlightNumber(),
                request.getFlightOrigin(),
                request.getFlightDestination());

        FlightPredictionResponseDTO response = predictionService.predictDelay(request);

        log.info("Prediction completed for flight {}: {} with probability {}",
                request.getFlightNumber(),
                response.getPrediction(),
                response.getProbability());

        return ResponseEntity.ok(response);
    }

    /**
     * 
     * @return ResponseEntity with status message and HTTP 200
     */
    @Operation(summary = "Health check", description = "Verifies that the API is running and ready to accept requests. Used by monitoring tools and load balancers.")
    @ApiResponse(responseCode = "200", description = "API is healthy and operational", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "status": "UP",
              "message": "FlightOnTime API is running",
              "timestamp": "2025-12-17T14:30:00"
            }
            """)))
    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        log.debug("Health check endpoint called");

        HealthCheckResponse response = HealthCheckResponse.builder()
                .status("UP")
                .message("FlightOnTime API is running")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Simple DTO for health check response.
     */
    @Schema(description = "Health check response")
    private record HealthCheckResponse(
            @Schema(description = "Service status", example = "UP") String status,

            @Schema(description = "Status message", example = "FlightOnTime API is running") String message,

            @Schema(description = "Current timestamp", example = "2025-12-17T14:30:00") java.time.LocalDateTime timestamp) {
        /**
         */
        public static HealthCheckResponseBuilder builder() {
            return new HealthCheckResponseBuilder();
        }

        public static class HealthCheckResponseBuilder {
            private String status;
            private String message;
            private java.time.LocalDateTime timestamp;

            public HealthCheckResponseBuilder status(String status) {
                this.status = status;
                return this;
            }

            public HealthCheckResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public HealthCheckResponseBuilder timestamp(java.time.LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public HealthCheckResponse build() {
                return new HealthCheckResponse(status, message, timestamp);
            }
        }
    }
}

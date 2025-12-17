package com.backend.fot.controller;

import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.dto.FlightPredictionResponseDTO;
import com.backend.fot.enums.FlightPrediction;
import com.backend.fot.service.PredictionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PredictionController.
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-17
 */
@WebMvcTest(controllers = PredictionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class
})
@DisplayName("PredictionController Tests")
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PredictionService predictionService;

    private FlightPredictionRequestDTO createValidRequest() {
        return FlightPredictionRequestDTO.builder()
                .flightNumber("AA1234")
                .companyName("AA")
                .flightOrigin("GIG")
                .flightDestination("GRU")
                .flightDepartureDate(LocalDateTime.now().plusDays(1))
                .flightDistance(350)
                .build();
    }

    private FlightPredictionResponseDTO createMockResponse() {
        return FlightPredictionResponseDTO.builder()
                .prediction(FlightPrediction.ON_TIME)
                .probability(0.85)
                .confidence(FlightPredictionResponseDTO.ConfidenceLevel.HIGH)
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/predict - Happy Path")
    class HappyPathTests {

        @Test
        @DisplayName("Should return 200 OK with prediction when request is valid")
        void shouldReturnSuccessfulPrediction() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            FlightPredictionResponseDTO mockResponse = createMockResponse();

            when(predictionService.predictDelay(any(FlightPredictionRequestDTO.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.prediction").value("ON_TIME"))
                    .andExpect(jsonPath("$.probability").value(0.85))
                    .andExpect(jsonPath("$.confidence").value("HIGH"));

            verify(predictionService, times(1)).predictDelay(any(FlightPredictionRequestDTO.class));
        }

        @Test
        @DisplayName("Should return DELAYED prediction with high probability")
        void shouldReturnDelayedPrediction() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            FlightPredictionResponseDTO delayedResponse = FlightPredictionResponseDTO.builder()
                    .prediction(FlightPrediction.DELAYED)
                    .probability(0.92)
                    .confidence(FlightPredictionResponseDTO.ConfidenceLevel.VERY_HIGH)
                    .build();

            when(predictionService.predictDelay(any(FlightPredictionRequestDTO.class)))
                    .thenReturn(delayedResponse);

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.prediction").value("DELAYED"))
                    .andExpect(jsonPath("$.probability").value(0.92))
                    .andExpect(jsonPath("$.confidence").value("VERY_HIGH"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/predict - Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should return 400 when flight number is missing")
        void shouldRejectMissingFlightNumber() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            request = request.toBuilder().flightNumber(null).build();

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }

        @Test
        @DisplayName("Should return 400 when flight origin has invalid format")
        void shouldRejectInvalidFlightOrigin() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            request = request.toBuilder().flightOrigin("AB").build(); // Only 2 chars

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }

        @Test
        @DisplayName("Should return 400 when flight distance is negative")
        void shouldRejectNegativeDistance() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            request = request.toBuilder().flightDistance(-100).build();

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }

        @Test
        @DisplayName("Should return 400 when departure date is in the past")
        void shouldRejectPastDepartureDate() throws Exception {
            // Arrange
            FlightPredictionRequestDTO request = createValidRequest();
            request = request.toBuilder()
                    .flightDepartureDate(LocalDateTime.now().minusDays(1))
                    .build();

            // Act & Assert
            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/health")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return 200 OK with status UP")
        void shouldReturnHealthyStatus() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/v1/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.message").value("FlightOnTime API is running"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        @DisplayName("Should not call service layer for health check")
        void shouldNotCallServiceForHealthCheck() throws Exception {
            mockMvc.perform(get("/api/v1/health"))
                    .andExpect(status().isOk());

            verifyNoInteractions(predictionService);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty request body with 400")
        void shouldRejectEmptyBody() throws Exception {
            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }

        @Test
        @DisplayName("Should handle malformed JSON with 400")
        void shouldRejectMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid json}"))
                    .andExpect(status().isBadRequest());

            verify(predictionService, never()).predictDelay(any());
        }

        @Test
        @DisplayName("Should accept minimum valid distance (1 km)")
        void shouldAcceptMinimumDistance() throws Exception {
            FlightPredictionRequestDTO request = createValidRequest();
            request = request.toBuilder().flightDistance(1).build();

            when(predictionService.predictDelay(any())).thenReturn(createMockResponse());

            mockMvc.perform(post("/api/v1/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(predictionService, times(1)).predictDelay(any());
        }
    }
}

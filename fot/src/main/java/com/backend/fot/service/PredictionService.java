package com.backend.fot.service;

import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.dto.FlightPredictionResponseDTO;

/**
 * Service interface for flight delay predictions.
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-17
 */
public interface PredictionService {

    /**
     * Predicts flight delay based on provided flight information.
     * 
     * @param request
     * 
     * @return
     * @throws com.backend.fot.exceptions.MLServiceException
     * @throws IllegalArgumentException
     * 
     */
    FlightPredictionResponseDTO predictDelay(FlightPredictionRequestDTO request);
}

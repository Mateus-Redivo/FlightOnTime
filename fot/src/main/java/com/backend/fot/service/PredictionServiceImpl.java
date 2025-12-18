package com.backend.fot.service;

import com.backend.fot.dto.FlightPredictionRequestDTO;
import com.backend.fot.dto.FlightPredictionResponseDTO;
import com.backend.fot.enums.FlightPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of PredictionService for flight delay predictions.
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-17
 */
@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {

    /**
     * Predicts flight delay using ML service.
     * 
     * @param request Flight information
     * @return Prediction with delay status and probability
     */
    @Override
    public FlightPredictionResponseDTO predictDelay(FlightPredictionRequestDTO request) {
        log.info("Processing prediction for flight {}", request.getFlightNumber());

        FlightPrediction prediction = determinePrediction(request);
        Double probability = calculateProbability(request);

        log.debug("Prediction result: {} with probability {}", prediction, probability);

        return FlightPredictionResponseDTO.builder()
                .prediction(prediction)
                .probability(probability)
                .confidence(determineConfidenceLevel(probability))
                .build();
    }

    /**
     * @param request Flight information
     * @return Predicted status (ON_TIME or DELAYED)
     */
    private FlightPrediction determinePrediction(FlightPredictionRequestDTO request) {
        int distance = request.getFlightDistance();

        if (distance > 1000) {
            return FlightPrediction.DELAYED;
        } else {
            return FlightPrediction.ON_TIME;
        }
    }

    /**
     * Calculates prediction probability.
     * 
     * @param request Flight information
     * @return Probability value between 0.0 and 1.0
     */
    private Double calculateProbability(FlightPredictionRequestDTO request) {
        int distance = request.getFlightDistance();

        double baseProbability = 0.5;
        double distanceFactor = Math.min(distance / 5000.0, 0.45);

        BigDecimal probability = BigDecimal.valueOf(baseProbability + distanceFactor)
                .setScale(2, RoundingMode.HALF_UP);

        return probability.doubleValue();
    }

    /**
     * Determines confidence level based on probability.
     * 
     * @param probability Prediction probability
     * @return Confidence level enum
     */
    private FlightPredictionResponseDTO.ConfidenceLevel determineConfidenceLevel(Double probability) {
        if (probability >= 0.90) {
            return FlightPredictionResponseDTO.ConfidenceLevel.VERY_HIGH;
        } else if (probability >= 0.75) {
            return FlightPredictionResponseDTO.ConfidenceLevel.HIGH;
        } else if (probability >= 0.60) {
            return FlightPredictionResponseDTO.ConfidenceLevel.MEDIUM;
        } else if (probability >= 0.45) {
            return FlightPredictionResponseDTO.ConfidenceLevel.LOW;
        } else {
            return FlightPredictionResponseDTO.ConfidenceLevel.VERY_LOW;
        }
    }
}

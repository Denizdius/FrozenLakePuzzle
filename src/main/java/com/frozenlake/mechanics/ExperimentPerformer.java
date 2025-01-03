package com.frozenlake.mechanics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.frozenlake.exceptions.GameException;

public class ExperimentPerformer {
    private static final String ICE_BLOCK = "IB";
    private static final String CLIFF_EDGE = "CE";
    private static final String EMPTY = "  ";
    private static final String HOLE_IN_ICE = "HI";
    private static final String ICE_SPIKES = "IS";

    private final Map<String, StringBuilder> experimentResults;
    private final Random random;

    public ExperimentPerformer() {
        this.experimentResults = new HashMap<>();
        this.random = new Random();
    }

    public boolean performExperiment(String[][] lake, String researcher, ExperimentGoals.ExperimentType type, int row, int col) 
            throws GameException {
        validatePosition(row, col, lake.length, lake[0].length);

        switch (type) {
            case TEMPERATURE_MEASUREMENT:
                if (!isValidForTemperatureMeasurement(lake, row, col)) {
                    throw new GameException("Invalid location for Temperature Measurement - needs open space away from edges");
                }
                recordTemperatureMeasurement(researcher);
                return true;

            case WINDSPEED_MEASUREMENT:
                if (!isValidForWindSpeedMeasurement(lake, row, col)) {
                    throw new GameException("Invalid location for Wind Speed Measurement - cannot measure on ice blocks");
                }
                recordWindSpeedMeasurement(researcher);
                return true;

            case CAMERA_PLACEMENT:
                if (!isValidForCameraPlacement(lake, row, col)) {
                    throw new GameException("Invalid location for Camera Placement - needs clear line of sight");
                }
                recordCameraPlacement(researcher);
                return true;

            case GLACIAL_SAMPLING:
                if (!isValidForGlacialSampling(lake, row, col)) {
                    throw new GameException("Invalid location for Glacial Sampling - must be on an ice block");
                }
                recordGlacialSampling(researcher);
                return true;

            default:
                throw new GameException("Unknown experiment type");
        }
    }

    private boolean isValidForTemperatureMeasurement(String[][] lake, int row, int col) {
        // Must be performed on empty spaces, not on the edges
        return lake[row][col].equals(EMPTY) && 
               row > 0 && row < lake.length - 1 && 
               col > 0 && col < lake[0].length - 1;
    }

    private void recordTemperatureMeasurement(String researcher) {
        int temperature = random.nextInt(31) - 30; // Random value between -30 and 0
        addResult(researcher, String.format("Temperature Measurement: %d°C", temperature));
    }

    private boolean isValidForWindSpeedMeasurement(String[][] lake, int row, int col) {
        // Can be performed anywhere except on ice blocks
        return !lake[row][col].equals(ICE_BLOCK);
    }

    private void recordWindSpeedMeasurement(String researcher) {
        int windSpeed = random.nextInt(31); // Random value between 0 and 30 m/s
        addResult(researcher, String.format("Wind Speed Measurement: %d m/s", windSpeed));
    }

    private boolean isValidForCameraPlacement(String[][] lake, int row, int col) {
        // Must be on empty space with no hazards in line of sight
        if (!lake[row][col].equals(EMPTY)) {
            return false;
        }
        return !hasHazardInLineOfSight(lake, row, col);
    }

    private void recordCameraPlacement(String researcher) {
        boolean isWorking = random.nextInt(5) > 0; // 80% success rate
        addResult(researcher, "Camera Placement: " + (isWorking ? "Success" : "Failure"));
    }

    private boolean isValidForGlacialSampling(String[][] lake, int row, int col) {
        // Must be performed on ice blocks
        return lake[row][col].equals(ICE_BLOCK);
    }

    private void recordGlacialSampling(String researcher) {
        int sampleWeight = random.nextInt(20) + 1; // Random weight between 1 and 20 grams
        addResult(researcher, String.format("Glacial Sampling: %d grams collected", sampleWeight));
    }

    private boolean hasHazardInLineOfSight(String[][] lake, int row, int col) {
        // Check for hazards in the same row to the right
        for (int j = col + 1; j < lake[0].length; j++) {
            String cell = lake[row][j];
            if (isHazard(cell)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHazard(String cell) {
        return cell.equals(ICE_BLOCK) || 
               cell.equals(HOLE_IN_ICE) || 
               cell.equals(ICE_SPIKES) || 
               cell.equals(CLIFF_EDGE);
    }

    private void validatePosition(int row, int col, int maxRow, int maxCol) throws GameException {
        if (row < 0 || row >= maxRow || col < 0 || col >= maxCol) {
            throw new GameException("Invalid position for experiment");
        }
    }

    private void addResult(String researcher, String result) {
        experimentResults.computeIfAbsent(researcher, k -> new StringBuilder())
                        .append(result).append("\n");
    }

    public void printResults() {
        if (experimentResults.isEmpty()) {
            System.out.println("No experiments have been performed yet.");
            return;
        }

        System.out.println("\n=== Experiment Results ===");
        experimentResults.forEach((researcher, results) -> {
            System.out.println("\nResearcher " + researcher + " Results:");
            System.out.println(results.toString());
        });
    }

    public void clearResults() {
        experimentResults.clear();
    }
}
package com.frozenlake.mechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExperimentGoals {
    public enum ExperimentType {
        TEMPERATURE_MEASUREMENT("Temperature Measurement"),
        WINDSPEED_MEASUREMENT("Wind Speed Measurement"),
        CAMERA_PLACEMENT("Camera Placement"),
        GLACIAL_SAMPLING("Glacial Sampling");

        private final String displayName;

        ExperimentType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private List<ExperimentType> experimentGoals;

    public ExperimentGoals() {
        experimentGoals = new ArrayList<>();
    }

    public void generateExperimentGoals(int numberOfResearchers) {
        Random random = new Random();
        // Number of experiments should be between (numberOfResearchers - 1) and numberOfResearchers
        int numberOfExperiments = random.nextInt(2) + (numberOfResearchers - 1);

        // Clear any existing goals
        experimentGoals.clear();

        // Ensure no duplicates and randomly select experiment types
        while (experimentGoals.size() < Math.min(numberOfExperiments, ExperimentType.values().length)) {
            ExperimentType experiment = ExperimentType.values()[random.nextInt(ExperimentType.values().length)];
            if (!experimentGoals.contains(experiment)) {
                experimentGoals.add(experiment);
            }
        }
    }

    public List<ExperimentType> getExperimentGoals() {
        return new ArrayList<>(experimentGoals);
    }

    public boolean isExperimentRequired(ExperimentType type) {
        return experimentGoals.contains(type);
    }

    public void clearExperimentGoals() {
        experimentGoals.clear();
    }
}
package com.frozenlake.mechanics;

import com.frozenlake.exceptions.GameException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractExperimentManager implements IExperimentPerformer {
    protected Map<String, Map<ExperimentGoals.ExperimentType, Boolean>> experimentResults;
    
    protected AbstractExperimentManager() {
        this.experimentResults = new HashMap<>();
    }
    
    @Override
    public abstract boolean performExperiment(String[][] lake, String researcher, 
            ExperimentGoals.ExperimentType type, int row, int col) throws GameException;
    
    @Override
    public void printResults() {
        experimentResults.forEach((researcher, experiments) -> {
            System.out.println("Researcher " + researcher + " experiments:");
            experiments.forEach((type, success) -> 
                System.out.println("- " + type + ": " + (success ? "Success" : "Failed")));
        });
    }
    
    @Override
    public void clearResults() {
        experimentResults.clear();
    }
    
    protected void recordResult(String researcher, ExperimentGoals.ExperimentType type, boolean success) {
        experimentResults.computeIfAbsent(researcher, k -> new HashMap<>()).put(type, success);
    }
    
    protected abstract boolean validateExperimentLocation(String[][] lake, int row, int col, 
            ExperimentGoals.ExperimentType type);
} 
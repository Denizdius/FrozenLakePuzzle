package com.frozenlake.mechanics;

import java.util.*;
import com.frozenlake.model.Equipment;

public class GameState {
    private final Map<String, Set<ExperimentGoals.ExperimentType>> completedExperiments;
    private final Set<String> activeResearchers;
    private final Set<String> finishedResearchers;
    private final List<ExperimentGoals.ExperimentType> requiredExperiments;
    private int score;

    public GameState(List<ExperimentGoals.ExperimentType> requiredExperiments, Queue<String> researchers) {
        this.requiredExperiments = new ArrayList<>(requiredExperiments);
        this.completedExperiments = new HashMap<>();
        this.activeResearchers = new HashSet<>();
        this.finishedResearchers = new HashSet<>();
        this.score = 100; // Start with 100 points
        
        // Initialize active researchers
        researchers.forEach(r -> {
            activeResearchers.add(r);
            completedExperiments.put(r, new HashSet<>());
        });
    }

    public void recordExperiment(String researcher, ExperimentGoals.ExperimentType experimentType) {
        completedExperiments.get(researcher).add(experimentType);
        checkExperimentCompletion();
    }

    public void researcherFinished(String researcher) {
        activeResearchers.remove(researcher);
        finishedResearchers.add(researcher);
    }

    public void researcherFell(String researcher) {
        activeResearchers.remove(researcher);
        score -= 20; // Penalty for falling
    }

    public void useEquipment(Equipment equipment) {
        // Small score penalty for using equipment
        score -= 5;
    }

    public boolean isGameOver() {
        return activeResearchers.isEmpty() || isAllExperimentsCompleted();
    }

    public boolean isAllExperimentsCompleted() {
        Set<ExperimentGoals.ExperimentType> allCompleted = new HashSet<>();
        completedExperiments.values().forEach(allCompleted::addAll);
        return allCompleted.containsAll(requiredExperiments);
    }

    public int getScore() {
        return Math.max(0, score);
    }

    public Set<String> getActiveResearchers() {
        return new HashSet<>(activeResearchers);
    }

    public Set<String> getFinishedResearchers() {
        return new HashSet<>(finishedResearchers);
    }

    public Set<ExperimentGoals.ExperimentType> getCompletedExperiments(String researcher) {
        return new HashSet<>(completedExperiments.getOrDefault(researcher, new HashSet<>()));
    }

    private void checkExperimentCompletion() {
        // Bonus points for completing experiments
        Set<ExperimentGoals.ExperimentType> allCompleted = new HashSet<>();
        completedExperiments.values().forEach(allCompleted::addAll);
        
        for (ExperimentGoals.ExperimentType experiment : requiredExperiments) {
            if (allCompleted.contains(experiment)) {
                score += 10; // Bonus for each completed experiment
            }
        }
    }
} 
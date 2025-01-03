package com.frozenlake.mechanics;

import java.util.*;
import com.frozenlake.model.Equipment;
import com.frozenlake.model.Researcher;
import com.frozenlake.exceptions.GameException;

public class GameState {
    private final Map<String, Set<ExperimentGoals.ExperimentType>> completedExperiments;
    private final Set<String> activeResearchers;
    private final Set<String> finishedResearchers;
    private final List<ExperimentGoals.ExperimentType> requiredExperiments;
    private int score;

    public GameState(List<ExperimentGoals.ExperimentType> requiredExperiments, Queue<Researcher> researchers) {
        if (requiredExperiments == null || researchers == null) {
            throw new IllegalArgumentException("Required experiments and researchers cannot be null");
        }
        
        this.requiredExperiments = new ArrayList<>(requiredExperiments);
        this.completedExperiments = new HashMap<>();
        this.activeResearchers = new HashSet<>();
        this.finishedResearchers = new HashSet<>();
        this.score = 100; // Start with 100 points
        
        // Initialize active researchers
        researchers.forEach(r -> {
            if (r != null) {
                activeResearchers.add(r.getId());
                completedExperiments.put(r.getId(), new HashSet<>());
            }
        });
        
        if (activeResearchers.isEmpty()) {
            throw new IllegalArgumentException("No valid researchers provided");
        }
    }

    public void recordExperiment(String researcher, ExperimentGoals.ExperimentType experimentType) throws GameException {
        if (researcher == null || experimentType == null) {
            throw new GameException("Researcher ID and experiment type cannot be null");
        }
        
        Set<ExperimentGoals.ExperimentType> experiments = completedExperiments.get(researcher);
        if (experiments == null) {
            throw new GameException("Unknown researcher: " + researcher);
        }
        
        experiments.add(experimentType);
        checkExperimentCompletion();
    }

    public void researcherFinished(String researcher) throws GameException {
        if (researcher == null || !activeResearchers.contains(researcher)) {
            throw new GameException("Invalid researcher ID: " + researcher);
        }
        activeResearchers.remove(researcher);
        finishedResearchers.add(researcher);
    }

    public void researcherFell(String researcher) throws GameException {
        if (researcher == null || !activeResearchers.contains(researcher)) {
            throw new GameException("Invalid researcher ID: " + researcher);
        }
        activeResearchers.remove(researcher);
        score -= 20; // Penalty for falling
    }

    public void useEquipment(Equipment equipment) throws GameException {
        if (equipment == null) {
            throw new GameException("Equipment cannot be null");
        }
        score -= 5; // Small score penalty for using equipment
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
        Set<ExperimentGoals.ExperimentType> allCompleted = new HashSet<>();
        completedExperiments.values().forEach(allCompleted::addAll);
        
        for (ExperimentGoals.ExperimentType experiment : requiredExperiments) {
            if (allCompleted.contains(experiment)) {
                score += 10; // Bonus for each completed experiment
            }
        }
    }
} 
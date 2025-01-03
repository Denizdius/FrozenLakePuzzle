package com.frozenlake.mechanics;

import com.frozenlake.model.Equipment;
import com.frozenlake.util.GameConstants;
import java.util.*;

public abstract class AbstractGameState implements IGameState {
    protected int score;
    protected Set<String> activeResearchers;
    protected Set<String> finishedResearchers;
    protected Map<String, Set<ExperimentGoals.ExperimentType>> completedExperiments;
    
    protected AbstractGameState() {
        this.score = GameConstants.INITIAL_SCORE;
        this.activeResearchers = new HashSet<>();
        this.finishedResearchers = new HashSet<>();
        this.completedExperiments = new HashMap<>();
    }
    
    @Override
    public void recordExperiment(String researcher, ExperimentGoals.ExperimentType experimentType) {
        completedExperiments.computeIfAbsent(researcher, k -> new HashSet<>()).add(experimentType);
        score += GameConstants.EXPERIMENT_COMPLETION_BONUS;
    }
    
    @Override
    public void researcherFinished(String researcher) {
        activeResearchers.remove(researcher);
        finishedResearchers.add(researcher);
    }
    
    @Override
    public void researcherFell(String researcher) {
        activeResearchers.remove(researcher);
        score -= GameConstants.FALL_PENALTY;
    }
    
    @Override
    public void useEquipment(Equipment equipment) {
        score -= GameConstants.EQUIPMENT_USE_PENALTY;
    }
    
    @Override
    public boolean isGameOver() {
        return activeResearchers.isEmpty() || score <= 0;
    }
    
    @Override
    public int getScore() {
        return score;
    }
    
    @Override
    public Set<String> getActiveResearchers() {
        return new HashSet<>(activeResearchers);
    }
    
    @Override
    public Set<String> getFinishedResearchers() {
        return new HashSet<>(finishedResearchers);
    }
    
    @Override
    public Set<ExperimentGoals.ExperimentType> getCompletedExperiments(String researcher) {
        return new HashSet<>(completedExperiments.getOrDefault(researcher, new HashSet<>()));
    }
    
    protected void addActiveResearcher(String researcher) {
        activeResearchers.add(researcher);
        completedExperiments.putIfAbsent(researcher, new HashSet<>());
    }
} 
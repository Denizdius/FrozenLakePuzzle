package com.frozenlake.mechanics;

import java.util.Set;
import com.frozenlake.model.Equipment;

public interface IGameState {
    void recordExperiment(String researcher, ExperimentGoals.ExperimentType experimentType);
    void researcherFinished(String researcher);
    void researcherFell(String researcher);
    void useEquipment(Equipment equipment);
    boolean isGameOver();
    boolean isAllExperimentsCompleted();
    int getScore();
    Set<String> getActiveResearchers();
    Set<String> getFinishedResearchers();
    Set<ExperimentGoals.ExperimentType> getCompletedExperiments(String researcher);
} 
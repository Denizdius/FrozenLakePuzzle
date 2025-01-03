package com.frozenlake.mechanics;

import com.frozenlake.exceptions.GameException;

public interface IExperimentPerformer {
    boolean performExperiment(String[][] lake, String researcher, ExperimentGoals.ExperimentType type, int row, int col) 
            throws GameException;
    void printResults();
    void clearResults();
} 
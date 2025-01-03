package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;

public class HazardEquipment extends Equipment {
    public HazardEquipment(String name, String shortName) {
        super(name, shortName);
    }

    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        return null; // Hazard equipment does not have an associated experiment type
    }
}
package com.frozenlake.model;

import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.mechanics.ExperimentGoals;

public class HazardEquipment extends Equipment {
    public HazardEquipment(String name, String shortName) {
        super(name, shortName);
    }

    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        return null; // Hazard equipment does not have an associated experiment type
    }

    @Override
    public boolean canUseOn(Lake lake, Position position) throws EquipmentException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canUseOn'");
    }

    @Override
    public void use(Lake lake, Position position) throws EquipmentException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'use'");
    }
}
package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;

public abstract class HazardHandlingEquipment extends Equipment {
    private final String handledHazardType;

    protected HazardHandlingEquipment(String name, String shortName, String handledHazardType) {
        super(name, shortName);
        this.handledHazardType = handledHazardType;
    }

    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        return null; // Hazard handling equipment doesn't perform experiments
    }

    public String getHandledHazardType() {
        return handledHazardType;
    }

    @Override
    public boolean canUseOn(Lake lake, Position position) throws EquipmentException {
        if (isUsed()) {
            throw new EquipmentException(getName() + " has already been used");
        }
        
        if (!isValidPosition(lake, position)) {
            return false;
        }
        
        return lake.getCell(position).equals(handledHazardType);
    }

    @Override
    public abstract void use(Lake lake, Position position) throws EquipmentException;
} 
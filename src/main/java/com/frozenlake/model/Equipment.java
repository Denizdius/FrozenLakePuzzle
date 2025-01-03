package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;

public abstract class Equipment extends AbstractEquipment implements IEquipment {
    
    public Equipment(String name, String shortName) {
        super(name, shortName);
    }
    
    @Override
    public abstract ExperimentGoals.ExperimentType getExperimentType();
    
    @Override
    public abstract boolean canUseOn(Lake lake, Position position) throws EquipmentException;
    
    @Override
    public abstract void use(Lake lake, Position position) throws EquipmentException;
}
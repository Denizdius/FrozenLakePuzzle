package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.util.GameConstants;

public abstract class AbstractEquipment {
    private final String name;
    private final String shortName;
    private boolean isUsed;

    protected AbstractEquipment(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
        this.isUsed = false;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isUsed() {
        return isUsed;
    }

    protected void setUsed() {
        this.isUsed = true;
    }

    @Override
    public String toString() {
        return name + (isUsed ? " (Used)" : "");
    }

    public abstract ExperimentGoals.ExperimentType getExperimentType();
    
    public abstract boolean canUseOn(Lake lake, Position position) throws EquipmentException;
    
    public abstract void use(Lake lake, Position position) throws EquipmentException;
    
    protected boolean isValidPosition(Lake lake, Position position) {
        return position != null && 
               position.isValid(lake.getRows(), lake.getColumns());
    }
    
    protected boolean isEmptyCell(Lake lake, Position position) throws EquipmentException {
        return lake.getCell(position).equals(GameConstants.EMPTY);
    }
    
    protected boolean isHazard(Lake lake, Position position) throws EquipmentException {
        return lake.isHazard(position);
    }
} 
package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.util.GameConstants;
import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.exceptions.GameException;

public abstract class Equipment {
    private final String name;
    private final String shortName;
    private boolean used;

    protected Equipment(String name, String shortName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (shortName == null || shortName.trim().isEmpty()) {
            throw new IllegalArgumentException("Short name cannot be null or empty");
        }
        this.name = name;
        this.shortName = shortName;
        this.used = false;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isUsed() {
        return used;
    }

    protected void setUsed() {
        this.used = true;
    }

    protected boolean isValidPosition(Lake lake, Position position) {
        return position != null && 
               position.isValid(lake.getRows(), lake.getColumns());
    }

    protected boolean isEmptyCell(Lake lake, Position position) throws EquipmentException {
        try {
            return lake.getCell(position).equals(GameConstants.EMPTY);
        } catch (Exception e) {
            throw new EquipmentException("Error checking cell: " + e.getMessage());
        }
    }

    public abstract ExperimentGoals.ExperimentType getExperimentType();
    public abstract boolean canUseOn(Lake lake, Position position) throws EquipmentException, GameException;
    public abstract void use(Lake lake, Position position) throws EquipmentException;
}
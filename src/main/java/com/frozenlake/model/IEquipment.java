package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;

public interface IEquipment {
    String getName();
    String getShortName();
    boolean isUsed();
    ExperimentGoals.ExperimentType getExperimentType();
    boolean canUseOn(Lake lake, Position position) throws EquipmentException;
    void use(Lake lake, Position position) throws EquipmentException;
} 
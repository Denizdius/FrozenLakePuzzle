package com.frozenlake.model;

import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.util.GameConstants;

public class ClimbingEquipment extends HazardHandlingEquipment {
    public ClimbingEquipment() {
        super("Climbing Equipment", GameConstants.CLIMBING, GameConstants.ICE_SPIKES);
    }

    @Override
    public void use(Lake lake, Position position) throws EquipmentException {
        if (!canUseOn(lake, position)) {
            throw new EquipmentException("Cannot use climbing equipment here");
        }
        lake.setCell(position, GameConstants.CLIMBED);
        setUsed();
    }
} 
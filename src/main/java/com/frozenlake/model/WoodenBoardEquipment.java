package com.frozenlake.model;

import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.exceptions.GameException;
import com.frozenlake.util.GameConstants;

public class WoodenBoardEquipment extends HazardHandlingEquipment {
    public WoodenBoardEquipment() {
        super("Large Wooden Board", GameConstants.WOODEN_BOARD, GameConstants.HOLE_IN_ICE);
    }

    @Override
    public void use(Lake lake, Position position) throws GameException {
        if (!canUseOn(lake, position)) {
            throw new EquipmentException("Cannot use wooden board here");
        }
        lake.setCell(position, GameConstants.BRIDGE);
        setUsed();
    }
} 
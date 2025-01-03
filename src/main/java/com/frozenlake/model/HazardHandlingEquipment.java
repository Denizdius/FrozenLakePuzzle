package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.exceptions.GameException;

/**
 * Abstract base class for equipment used to handle hazards in the game.
 * Extends Equipment to provide specific functionality for hazard management.
 */
public abstract class HazardHandlingEquipment extends Equipment {
    private final String handledHazardType;

    /**
     * Constructs a new HazardHandlingEquipment instance.
     *
     * @param name The display name of the equipment
     * @param shortName The short code used to reference the equipment
     * @param handledHazardType The type of hazard this equipment can handle
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    protected HazardHandlingEquipment(String name, String shortName, String handledHazardType) {
        super(name, shortName);
        if (handledHazardType == null || handledHazardType.trim().isEmpty()) {
            throw new IllegalArgumentException("Handled hazard type cannot be null or empty");
        }
        this.handledHazardType = handledHazardType;
    }

    /**
     * Returns null as hazard handling equipment doesn't perform experiments.
     *
     * @return null always
     */
    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        return null;
    }

    /**
     * Gets the type of hazard this equipment can handle.
     *
     * @return The hazard type string
     */
    public String getHandledHazardType() {
        return handledHazardType;
    }

    /**
     * Checks if the equipment can be used on a specific position in the lake.
     *
     * @param lake The lake where the equipment will be used
     * @param position The position where the equipment will be used
     * @return true if the equipment can be used at the specified position
     * @throws GameException if there's an error checking the position
     */
    @Override
    public boolean canUseOn(Lake lake, Position position) throws GameException {
        if (lake == null || position == null) {
            throw new EquipmentException("Lake and position must not be null");
        }

        if (isUsed()) {
            return false;
        }

        if (!isValidPosition(lake, position)) {
            return false;
        }
        
        return lake.getCell(position).equals(handledHazardType);
    }

    /**
     * Uses the equipment at the specified position in the lake.
     * Implementation must be provided by concrete classes.
     *
     * @param lake The lake where the equipment will be used
     * @param position The position where the equipment will be used
     * @throws EquipmentException if the equipment cannot be used
     * @throws GameException 
     */
    @Override
    public abstract void use(Lake lake, Position position) throws EquipmentException;

    @Override
    public String toString() {
        return String.format("%s (Handles: %s)%s", 
            getName(), 
            handledHazardType, 
            isUsed() ? " [USED]" : ""
        );
    }
}
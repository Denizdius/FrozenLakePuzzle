package com.frozenlake.model;

import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.util.GameConstants;
import com.frozenlake.mechanics.ExperimentGoals;
import java.util.Random;

public class ChiselingEquipment extends HazardHandlingEquipment {
    private final Random random = new Random();
    private Integer sampleWeight;

    public ChiselingEquipment() {
        super("Chiseling Equipment", GameConstants.CHISEL, GameConstants.ICE_BLOCK);
    }

    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        return ExperimentGoals.ExperimentType.GLACIAL_SAMPLING;
    }

    @Override
    public void use(Lake lake, Position position) throws EquipmentException {
        if (!canUseOn(lake, position)) {
            throw new EquipmentException("Cannot use chiseling equipment here");
        }
        
        // Take glacial sample (1-20 grams)
        sampleWeight = random.nextInt(20) + 1;
        
        // Clear the ice block after sampling
        lake.setCell(position, GameConstants.EMPTY);
        setUsed();
    }

    public Integer getSampleWeight() {
        return sampleWeight;
    }

    public String getFormattedResult() {
        if (sampleWeight == null) {
            return "No glacial sample taken";
        }
        return String.format("Glacial Sampling: %d grams collected", sampleWeight);
    }
} 
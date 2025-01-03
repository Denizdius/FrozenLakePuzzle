package com.frozenlake.model;

import com.frozenlake.mechanics.ExperimentGoals;
import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.util.GameConstants;
import java.util.Random;

public class ResearchEquipment extends Equipment {
    private final Random random = new Random();
    private Object experimentResult;

    public ResearchEquipment(String name, String shortName) {
        super(name, shortName);
    }

    @Override
    public ExperimentGoals.ExperimentType getExperimentType() {
        switch (getShortName()) {
            case GameConstants.TEMP_DETECTOR:
                return ExperimentGoals.ExperimentType.TEMPERATURE_MEASUREMENT;
            case GameConstants.WIND_DETECTOR:
                return ExperimentGoals.ExperimentType.WINDSPEED_MEASUREMENT;
            case GameConstants.CAMERA:
                return ExperimentGoals.ExperimentType.CAMERA_PLACEMENT;
            default:
                throw new IllegalArgumentException("Unknown research equipment: " + getShortName());
        }
    }

    @Override
    public boolean canUseOn(Lake lake, Position position) throws EquipmentException {
        if (isUsed()) {
            throw new EquipmentException(getName() + " has already been used");
        }

        if (!isValidPosition(lake, position)) {
            return false;
        }

        switch (getExperimentType()) {
            case TEMPERATURE_MEASUREMENT:
                return canPerformTemperatureMeasurement(lake, position);
            
            case WINDSPEED_MEASUREMENT:
                return canPerformWindSpeedMeasurement(lake, position);
            
            case CAMERA_PLACEMENT:
                return canPlaceCamera(lake, position);
            
            default:
                return false;
        }
    }

    @Override
    public void use(Lake lake, Position position) throws EquipmentException {
        if (!canUseOn(lake, position)) {
            throw new EquipmentException("Cannot use " + getName() + " here");
        }

        switch (getExperimentType()) {
            case TEMPERATURE_MEASUREMENT:
                experimentResult = random.nextInt(31) - 30; // -30 to 0 Celsius
                break;
            case WINDSPEED_MEASUREMENT:
                experimentResult = random.nextInt(31); // 0 to 30 m/s
                break;
            case CAMERA_PLACEMENT:
                experimentResult = random.nextDouble() > 0.2; // 80% success rate
                break;
            default:
                throw new EquipmentException("Unknown experiment type");
        }
        
        setUsed();
    }

    public Object getExperimentResult() {
        return experimentResult;
    }

    public String getFormattedResult() {
        if (experimentResult == null) {
            return "No experiment performed";
        }

        switch (getExperimentType()) {
            case TEMPERATURE_MEASUREMENT:
                return String.format("Temperature Measurement: %d°C", experimentResult);
            case WINDSPEED_MEASUREMENT:
                return String.format("Wind Speed Measurement: %d m/s", experimentResult);
            case CAMERA_PLACEMENT:
                return String.format("Camera Placement: %s", 
                    (Boolean)experimentResult ? "Success" : "The camera failed to start recording");
            default:
                return "Unknown experiment type";
        }
    }

    private boolean canPerformTemperatureMeasurement(Lake lake, Position position) throws EquipmentException {
        // Must be on empty space, not on edges, and not next to ice blocks
        if (!isEmptyCell(lake, position) || isOnEdge(lake, position)) {
            return false;
        }

        // Check adjacent cells for ice blocks
        for (Direction dir : Direction.values()) {
            Position adjacent = position.move(dir);
            if (adjacent.isValid(lake.getRows(), lake.getColumns()) &&
                lake.getCell(adjacent).equals(GameConstants.ICE_BLOCK)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPerformWindSpeedMeasurement(Lake lake, Position position) throws EquipmentException {
        // Can be performed anywhere except next to hazards (except ice blocks)
        if (!isEmptyCell(lake, position)) {
            return false;
        }

        // Check adjacent cells for hazards (except ice blocks)
        for (Direction dir : Direction.values()) {
            Position adjacent = position.move(dir);
            if (adjacent.isValid(lake.getRows(), lake.getColumns()) &&
                lake.isHazard(adjacent) &&
                !lake.getCell(adjacent).equals(GameConstants.ICE_BLOCK)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceCamera(Lake lake, Position position) throws EquipmentException {
        return isEmptyCell(lake, position) && !hasHazardInLineOfSight(lake, position);
    }

    private boolean hasHazardInLineOfSight(Lake lake, Position position) throws EquipmentException {
        Position current = new Position(position.getRow(), position.getCol());
        while (current.getCol() < lake.getColumns() - 1) {
            current = current.move(Direction.RIGHT);
            if (lake.getCell(current).equals(GameConstants.CLIFF_EDGE)) {
                return false;
            }
            if (!isEmptyCell(lake, current)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnEdge(Lake lake, Position position) {
        return position.getRow() == 0 || 
               position.getRow() == lake.getRows() - 1 || 
               position.getCol() == 0 || 
               position.getCol() == lake.getColumns() - 1;
    }
}
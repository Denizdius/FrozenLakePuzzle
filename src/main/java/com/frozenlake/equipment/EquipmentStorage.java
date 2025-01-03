package com.frozenlake.equipment;

import java.util.HashMap;
import java.util.Map;
import com.frozenlake.exceptions.EquipmentException;
import com.frozenlake.model.*;

public class EquipmentStorage {
    private Map<String, Equipment> availableEquipment;

    public EquipmentStorage() {
        availableEquipment = new HashMap<>();
        initializeEquipment();
    }

    private void initializeEquipment() {
        // Research Equipment
        availableEquipment.put("td", new ResearchEquipment("Temperature Detector", "td"));
        availableEquipment.put("ws", new ResearchEquipment("Wind Speed Detector", "ws"));
        availableEquipment.put("cm", new ResearchEquipment("Camera", "cm"));

        // Hazard Handling Equipment
        availableEquipment.put("ch", new ChiselingEquipment());
        availableEquipment.put("cl", new ClimbingEquipment());
        availableEquipment.put("wb", new WoodenBoardEquipment());
    }

    public boolean isAvailable(String shortName) {
        return availableEquipment.containsKey(shortName) && !availableEquipment.get(shortName).isUsed();
    }

    public Equipment getEquipment(String shortName) throws EquipmentException {
        if (!availableEquipment.containsKey(shortName)) {
            throw new EquipmentException("Equipment " + shortName + " does not exist");
        }
        if (availableEquipment.get(shortName).isUsed()) {
            throw new EquipmentException("Equipment " + shortName + " is already used");
        }
        return availableEquipment.get(shortName);
    }

    public void resetEquipment() {
        initializeEquipment();
    }
}
package com.frozenlake.equipment;

import java.util.ArrayList;
import java.util.List;
import com.frozenlake.exceptions.*;
import com.frozenlake.model.*;

public class EquipmentBag<T extends Equipment> {
    private static final int MAX_ITEMS = 3;
    private List<T> items;
    private Class<?> allowedType;

    public EquipmentBag() {
        items = new ArrayList<>();
        allowedType = null;
    }

    public void addEquipment(T equipment) throws GameException {
        if (items.size() >= MAX_ITEMS) {
            throw new IncorrectBagContentsException("Equipment bag is full (maximum " + MAX_ITEMS + " items)");
        }

        if (items.isEmpty()) {
            // First item determines the allowed type
            allowedType = equipment instanceof ResearchEquipment ? ResearchEquipment.class : HazardHandlingEquipment.class;
        } else if (!allowedType.isInstance(equipment)) {
            throw new IncorrectBagContentsException("Cannot mix different types of equipment in the same bag");
        }

        items.add(equipment);
    }

    public void validateContents() throws GameException {
        if (items.isEmpty()) {
            throw new IncorrectBagContentsException("Cannot proceed with an empty equipment bag");
        }
    }

    public List<T> getItems() {
        return new ArrayList<>(items);
    }

    public Equipment findEquipmentForHazard(String hazardType) {
        if (allowedType != HazardHandlingEquipment.class) {
            return null;
        }

        for (T equipment : items) {
            if (equipment instanceof HazardHandlingEquipment) {
                HazardHandlingEquipment hazardEquipment = (HazardHandlingEquipment) equipment;
                if (hazardEquipment.getHandledHazardType().equals(hazardType) && !hazardEquipment.isUsed()) {
                    return hazardEquipment;
                }
            }
        }
        return null;
    }

    public Equipment findResearchEquipment(String shortName) throws UnavailableEquipmentException {
        if (allowedType != ResearchEquipment.class) {
            throw new UnavailableEquipmentException("No research equipment available in this bag");
        }

        for (T equipment : items) {
            if (!equipment.isUsed() && equipment.getShortName().equals(shortName)) {
                return equipment;
            }
        }
        throw new UnavailableEquipmentException("Research equipment '" + shortName + "' not found or already used");
    }

    public boolean hasUnusedEquipment() {
        return items.stream().anyMatch(equipment -> !equipment.isUsed());
    }

    public boolean isResearchBag() {
        return allowedType == ResearchEquipment.class;
    }

    public boolean isHazardBag() {
        return allowedType == HazardHandlingEquipment.class;
    }

    public void clear() {
        items.clear();
        allowedType = null;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
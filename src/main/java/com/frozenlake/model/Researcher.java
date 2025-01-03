package com.frozenlake.model;

import java.util.HashSet;
import java.util.Set;
import com.frozenlake.equipment.EquipmentBag;
import com.frozenlake.mechanics.ExperimentGoals.ExperimentType;

public class Researcher extends AbstractGameEntity {
    private final String displayName;
    private EquipmentBag<Equipment> equipmentBag;
    private final Set<ExperimentType> completedExperiments;
    private boolean active;
    private boolean fallen;

    public Researcher(String id, Position position) {
        super(id, position);
        this.displayName = "Researcher " + id.substring(1); // Assumes format "R1", "R2", etc.
        this.equipmentBag = new EquipmentBag<>();
        this.completedExperiments = new HashSet<>();
        this.active = true;
        this.fallen = false;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public EquipmentBag<Equipment> getEquipmentBag() {
        return equipmentBag;
    }

    public void setEquipmentBag(EquipmentBag<Equipment> equipmentBag) {
        this.equipmentBag = equipmentBag;
    }

    public Set<ExperimentType> getCompletedExperiments() {
        return new HashSet<>(completedExperiments);
    }

    public void addCompletedExperiment(ExperimentType experiment) {
        completedExperiments.add(experiment);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean hasFallen() {
        return fallen;
    }

    public void setFallen(boolean fallen) {
        this.fallen = fallen;
        if (fallen) {
            this.active = false;
        }
    }

    public boolean hasUnusedEquipment() {
        return equipmentBag != null && equipmentBag.hasUnusedEquipment();
    }

    @Override
    public String toString() {
        return displayName;
    }
} 
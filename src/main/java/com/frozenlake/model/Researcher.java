package com.frozenlake.model;

import java.util.HashSet;
import java.util.Set;
import com.frozenlake.equipment.EquipmentBag;
import com.frozenlake.mechanics.ExperimentGoals.ExperimentType;

public class Researcher {
    private final String id;
    private final String displayName;
    private Position position;
    private EquipmentBag<Equipment> equipmentBag;
    private final Set<ExperimentType> completedExperiments;
    private boolean active;
    private boolean fallen;

    public Researcher(String id) {
        this.id = id;
        this.displayName = "Researcher " + id.substring(1); // Assumes format "R1", "R2", etc.
        this.equipmentBag = new EquipmentBag<>();
        this.completedExperiments = new HashSet<>();
        this.active = true;
        this.fallen = false;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Researcher)) return false;
        Researcher other = (Researcher) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 
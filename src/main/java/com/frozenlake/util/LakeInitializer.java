package com.frozenlake.util;

import com.frozenlake.model.Lake;
import com.frozenlake.model.Position;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class LakeInitializer {
    private final Random random = new Random();
    private final Lake lake;
    private final int cliffEdgePosition;

    public LakeInitializer(int rows, int columns) {
        this.lake = new Lake(rows, columns);
        this.cliffEdgePosition = random.nextInt(rows);
        initializeLake();
    }

    private void initializeLake() {
        // Initialize empty lake
        for (int i = 0; i < lake.getRows(); i++) {
            for (int j = 0; j < lake.getColumns(); j++) {
                lake.setCell(new Position(i, j), GameConstants.EMPTY);
            }
        }

        // Place entrance
        lake.setCell(new Position(0, 0), GameConstants.ENTRANCE);

        // Place cliff edges
        for (int i = 0; i < lake.getRows(); i++) {
            if (i >= cliffEdgePosition && i < cliffEdgePosition + 3) {
                lake.setCell(new Position(i, lake.getColumns() - 1), GameConstants.CLIFF_EDGE);
            }
        }

        // Place ice blocks (8)
        placeHazards(GameConstants.ICE_BLOCK, 8);

        // Place holes in ice (3)
        placeHazards(GameConstants.HOLE_IN_ICE, 3);

        // Place ice spikes (3)
        placeIceSpikes(3);
    }

    private void placeHazards(String hazardType, int count) {
        int placed = 0;
        while (placed < count) {
            Position pos = getRandomPosition();
            
            // Skip if too close to entrance
            if (isTooCloseToEntrance(pos)) {
                continue;
            }

            // Skip if next to cliff edge
            if (isNextToCliffEdge(pos)) {
                continue;
            }

            // Skip if position already occupied
            if (!lake.getCell(pos).equals(GameConstants.EMPTY)) {
                continue;
            }

            lake.setCell(pos, hazardType);
            placed++;
        }
    }

    private void placeIceSpikes(int count) {
        int placed = 0;
        while (placed < count) {
            Position pos = getRandomPosition();
            
            // Skip if too close to entrance
            if (isTooCloseToEntrance(pos)) {
                continue;
            }

            // Skip if next to cliff edge
            if (isNextToCliffEdge(pos)) {
                continue;
            }

            // Ice spikes must be next to walls (row 0 or last row)
            if (pos.getRow() != 0 && pos.getRow() != lake.getRows() - 1) {
                continue;
            }

            // Skip if position already occupied
            if (!lake.getCell(pos).equals(GameConstants.EMPTY)) {
                continue;
            }

            lake.setCell(pos, GameConstants.ICE_SPIKES);
            placed++;
        }
    }

    private Position getRandomPosition() {
        return new Position(
            random.nextInt(lake.getRows()),
            random.nextInt(lake.getColumns())
        );
    }

    private boolean isTooCloseToEntrance(Position pos) {
        return pos.getRow() < 3 && pos.getCol() < 3;
    }

    private boolean isNextToCliffEdge(Position pos) {
        // Check if position is next to cliff edge
        if (pos.getCol() == lake.getColumns() - 2) {
            for (int i = cliffEdgePosition; i < cliffEdgePosition + 3; i++) {
                if (Math.abs(pos.getRow() - i) <= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public Lake getLake() {
        return lake;
    }

    public int getCliffEdgePosition() {
        return cliffEdgePosition;
    }
}
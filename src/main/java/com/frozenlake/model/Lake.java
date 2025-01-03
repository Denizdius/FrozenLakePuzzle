package com.frozenlake.model;

import com.frozenlake.util.GameConstants;
import com.frozenlake.exceptions.GameException;

public class Lake {
    private final String[][] grid;
    private final int rows;
    private final int columns;

    public Lake(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new String[rows][columns];
        initializeEmpty();
    }

    private void initializeEmpty() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = GameConstants.EMPTY;
            }
        }
    }

    public String getCell(Position pos) throws GameException {
        validatePosition(pos);
        return grid[pos.getRow()][pos.getCol()];
    }

    public void setCell(Position pos, String value) throws GameException {
        validatePosition(pos);
        grid[pos.getRow()][pos.getCol()] = value;
    }

    public boolean isHazard(Position pos) throws GameException {
        String cell = getCell(pos);
        return cell.equals(GameConstants.CLIFF_EDGE) ||
               cell.equals(GameConstants.ICE_SPIKES) ||
               cell.equals(GameConstants.HOLE_IN_ICE) ||
               cell.equals(GameConstants.ICE_BLOCK);
    }

    public boolean canMoveThrough(Position pos) throws GameException {
        if (!pos.isValid(rows, columns)) return false;
        String cell = getCell(pos);
        return cell.equals(GameConstants.EMPTY) ||
               cell.equals(GameConstants.BRIDGE) ||
               cell.equals(GameConstants.CLIMBED) ||
               cell.equals(GameConstants.CLIFF_EDGE);
    }

    public Position findResearcher(String researcherId) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j].equals(researcherId)) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public void moveResearcher(String researcherId, Position from, Position to) throws GameException {
        validatePosition(from);
        validatePosition(to);
        
        if (grid[from.getRow()][from.getCol()].equals(researcherId)) {
            grid[from.getRow()][from.getCol()] = GameConstants.EMPTY;
            grid[to.getRow()][to.getCol()] = researcherId;
        } else {
            throw new GameException("Researcher not found at the specified position");
        }
    }

    private void validatePosition(Position pos) throws GameException {
        if (!pos.isValid(rows, columns)) {
            throw new GameException("Invalid position: " + pos);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public String[][] getGrid() {
        // Return a defensive copy
        String[][] copy = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, columns);
        }
        return copy;
    }
} 
package com.frozenlake.ui;

import com.frozenlake.model.Lake;
import com.frozenlake.model.Position;
import com.frozenlake.exceptions.GameException;
import com.frozenlake.util.GameConstants;

public class LakePrinter {
    private int rows;
    private int columns;

    public LakePrinter() {
        // Default constructor
    }

    public LakePrinter(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public void printLake(Lake lake) {
        this.rows = lake.getRows();
        this.columns = lake.getColumns();
        String[][] lakeArray = new String[rows][columns];
        
        // Convert Lake to array representation
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                try {
                    lakeArray[i][j] = lake.getCell(new Position(i, j));
                } catch (GameException e) {
                    // If there's an error getting the cell content, show empty cell
                    lakeArray[i][j] = GameConstants.EMPTY;
                }
            }
        }
        
        printLakeArray(lakeArray);
    }

    private void printLakeArray(String[][] lake) {
        // Create walls (top boundary)
        for (int j = 0; j < columns; j++) {
            System.out.print("---");
        }
        System.out.println();

        // Print the lake content
        for (int i = 0; i < rows; i++) {
            System.out.print("|");
            for (int j = 0; j < columns; j++) {
                String cell = lake[i][j];
                if (cell != null && !cell.isEmpty()) {
                    System.out.printf(" %s |", cell);
                } else {
                    System.out.print("   |");
                }
            }
            System.out.println();
            
            // Print horizontal boundary
            for (int j = 0; j < columns; j++) {
                System.out.print("---");
            }
            System.out.println();
        }
    }
}
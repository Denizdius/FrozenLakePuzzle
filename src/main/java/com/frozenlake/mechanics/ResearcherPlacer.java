package com.frozenlake.mechanics;
import java.util.LinkedList;
import java.util.Queue;

public class ResearcherPlacer {
    private static final String RESEARCHER1 = "R1"; // Researcher 1
    private static final String RESEARCHER2 = "R2"; // Researcher 2
    private static final String RESEARCHER3 = "R3"; // Researcher 3
    private static final String RESEARCHER4 = "R4"; // Researcher 4
    private static final String ENTRANCE = "E "; // Entrance
    private int ROWS;
    private int COLUMNS;

    public ResearcherPlacer(int rows, int columns) {
        this.ROWS = rows;
        this.COLUMNS = columns;
    }

    public void placeResearchers(String[][] lake, Queue<String> researchers) {

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {

                int[] a = { i, j }; // Store coordinates of lake[i][j]
                int entrance_row = 15;
                int entrance_column = 15;

                if (lake[i][j].equals(ENTRANCE)) {
                    entrance_row = a[0];
                    entrance_column = a[1];
                }
                

                if ((i == entrance_row && j == entrance_column)) {

                    if (!researchers.isEmpty()) {
                        String currentResearcher = researchers.poll(); // Safely retrieve and remove the next researcher
                        if (currentResearcher != null && currentResearcher.equals(RESEARCHER1)) {
                            lake[entrance_row][entrance_column] = RESEARCHER1; // Place the researcher
                        } else if (currentResearcher != null && currentResearcher.equals(RESEARCHER2)) {
                            lake[entrance_row][entrance_column] = RESEARCHER2; // Place the second researcher
                        } else if (currentResearcher != null && currentResearcher.equals(RESEARCHER3)) {
                            lake[entrance_row][entrance_column] = RESEARCHER3; 
                        } else if (currentResearcher != null && currentResearcher.equals(RESEARCHER4)) {
                            lake[entrance_row][entrance_column] = RESEARCHER4; 
                        } else {
                            System.out.println("No valid researcher to place.");
                        }
                    } else {
                        System.out.println("No researchers left in the queue to place.");
                    }
                    
                }
            }
        }
    }
}
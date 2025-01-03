package com.frozenlake.mechanics;

import java.util.Queue;
import java.util.Scanner;
import com.frozenlake.exceptions.*;
import com.frozenlake.equipment.*;
import com.frozenlake.model.*;
import com.frozenlake.ui.LakePrinter;

public class ResearcherMover {
    private static final String EMPTY = "  ";
    private static final String ENTRANCE = "E ";
    private static final String CLIFF_EDGE = "CE";
    private static final String ICE_BLOCK = "IB";
    private static final String ICE_SPIKES = "IS";
    private static final String HOLE_IN_ICE = "HI";
    private static final String BRIDGE = "BR";
    private static final String CLIMBED = "CL";

    private final int rows;
    private final int columns;
    private final EquipmentStorage equipmentStorage;
    private final GameState gameState;
    private final ExperimentPerformer experimentPerformer;
    private EquipmentBag<Equipment> currentEquipmentBag;

    public ResearcherMover(int rows, int columns, Queue<String> researchers, List<ExperimentGoals.ExperimentType> goals) {
        this.rows = rows;
        this.columns = columns;
        this.equipmentStorage = new EquipmentStorage();
        this.gameState = new GameState(goals, researchers);
        this.experimentPerformer = new ExperimentPerformer();
        this.currentEquipmentBag = null;
    }

    public void move(String[][] lake, Queue<String> researchers, LakePrinter lakePrinter, Scanner scanner) {
        while (!gameState.isGameOver() && !researchers.isEmpty()) {
            String currentResearcher = researchers.peek();
            System.out.println("\n=====> " + getResearcherName(currentResearcher) + "'s turn");
            
            // Equipment selection phase
            currentEquipmentBag = selectEquipment(currentResearcher, scanner);
            
            // Movement phase
            boolean continueMoving = true;
            while (continueMoving && !gameState.isGameOver()) {
                lakePrinter.printLake(lake);
                Position position = findResearcher(lake, currentResearcher);
                
                if (position == null) {
                    System.out.println("Researcher not found on the lake!");
                    break;
                }

                // Show available actions
                showAvailableActions(currentEquipmentBag);
                String action = scanner.nextLine().trim().toLowerCase();

                try {
                    switch (action) {
                        case "m": // Move
                            continueMoving = handleMovement(lake, currentResearcher, position, scanner);
                            break;
                        case "e": // Use equipment
                            handleEquipmentUsage(lake, currentResearcher, position, currentEquipmentBag, scanner);
                            break;
                        case "f": // Finish turn
                            continueMoving = false;
                            break;
                        case "q": // Quit game
                            System.out.println("Game ended by user.");
                            return;
                        default:
                            System.out.println("Invalid action. Please try again.");
                    }
                } catch (GameException e) {
                    System.out.println(e.getMessage());
                }
            }

            // End of turn
            if (!gameState.isGameOver()) {
                researchers.poll(); // Remove current researcher from queue
                if (researchers.isEmpty() && !gameState.isAllExperimentsCompleted()) {
                    // Reset queue if experiments not completed
                    gameState.getActiveResearchers().forEach(researchers::offer);
                }
            }
            
            // Clear current equipment bag at end of turn
            currentEquipmentBag = null;
        }

        // Game over
        printGameResults();
    }

    private EquipmentBag<Equipment> selectEquipment(String researcher, Scanner scanner) {
        EquipmentBag<Equipment> equipmentBag = new EquipmentBag<>();
        
        System.out.println("\nAvailable equipment:");
        System.out.println("[td] Temperature Detector");
        System.out.println("[ws] Wind Speed Detector");
        System.out.println("[cm] Camera");
        System.out.println("[ch] Chiseling Equipment");
        System.out.println("[cl] Climbing Equipment");
        System.out.println("[wb] Large Wooden Board");
        System.out.println("[no] Finish selection");

        while (true) {
            try {
                if (equipmentBag.getItems().size() >= 3) {
                    System.out.println("Maximum equipment capacity reached.");
                    break;
                }

                System.out.print("Select equipment (or 'no' to finish): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("no")) break;

                if (!equipmentStorage.isAvailable(choice)) {
                    System.out.println("Invalid or unavailable equipment.");
                    continue;
                }

                Equipment equipment = equipmentStorage.getEquipment(choice);
                equipmentBag.addEquipment(equipment);
                System.out.println("Added " + equipment.getName() + " to bag.");
                System.out.println("Current bag: " + equipmentBag.getItems());

            } catch (EquipmentException e) {
                System.out.println(e.getMessage());
            }
        }

        return equipmentBag;
    }

    private boolean handleMovement(String[][] lake, String researcher, Position position, Scanner scanner) 
            throws MovementException {
        System.out.println("Choose direction ([U]p, [D]own, [L]eft, [R]ight): ");
        String direction = scanner.nextLine().trim().toLowerCase();

        if (!direction.matches("[udlr]")) {
            throw new MovementException("Invalid direction. Use U, D, L, or R.");
        }

        Position newPosition = calculateNewPosition(lake, position, direction);
        
        // Check if movement is valid
        if (isValidMove(lake, newPosition)) {
            // Clear current position
            lake[position.row][position.col] = EMPTY;
            
            // Check for hazards at new position
            String cellContent = lake[newPosition.row][newPosition.col];
            if (isHazard(cellContent)) {
                if (cellContent.equals(CLIFF_EDGE)) {
                    handleHazard(lake, researcher, newPosition);
                    return false;
                } else {
                    // Try to handle other hazards with equipment
                    Equipment hazardEquipment = findHazardEquipment(currentEquipmentBag, cellContent);
                    if (hazardEquipment != null) {
                        try {
                            hazardEquipment.use(lake, newPosition.row, newPosition.col);
                            gameState.useEquipment(hazardEquipment);
                            System.out.println("Used " + hazardEquipment.getName() + " to handle hazard!");
                        } catch (EquipmentException e) {
                            System.out.println("Failed to use equipment: " + e.getMessage());
                            return false;
                        }
                    } else {
                        System.out.println("No suitable equipment to handle this hazard!");
                        return false;
                    }
                }
            }
            
            // Move researcher
            lake[newPosition.row][newPosition.col] = researcher;
            return true;
        }
        
        throw new MovementException("Cannot move in that direction.");
    }

    private Equipment findHazardEquipment(EquipmentBag<Equipment> equipmentBag, String hazardType) {
        if (equipmentBag == null) return null;
        return equipmentBag.findEquipmentForHazard(hazardType);
    }

    private Position calculateNewPosition(String[][] lake, Position start, String direction) {
        Position newPos = new Position(start.row, start.col);
        
        switch (direction) {
            case "u":
                while (newPos.row > 0 && canMoveThrough(lake[newPos.row - 1][newPos.col])) {
                    newPos.row--;
                }
                break;
            case "d":
                while (newPos.row < rows - 1 && canMoveThrough(lake[newPos.row + 1][newPos.col])) {
                    newPos.row++;
                }
                break;
            case "l":
                while (newPos.col > 0 && canMoveThrough(lake[newPos.row][newPos.col - 1])) {
                    newPos.col--;
                }
                break;
            case "r":
                while (newPos.col < columns - 1 && canMoveThrough(lake[newPos.row][newPos.col + 1])) {
                    newPos.col++;
                }
                break;
        }
        
        return newPos;
    }

    private boolean canMoveThrough(String cell) {
        return cell.equals(EMPTY) || cell.equals(BRIDGE) || cell.equals(CLIMBED) || cell.equals(CLIFF_EDGE);
    }

    private void handleEquipmentUsage(String[][] lake, String researcher, Position position, 
            EquipmentBag<Equipment> equipmentBag, Scanner scanner) throws EquipmentException {
        if (!equipmentBag.hasUnusedEquipment()) {
            throw new EquipmentException("No unused equipment available.");
        }

        System.out.println("\nAvailable equipment: ");
        equipmentBag.getItems().forEach(e -> {
            if (!e.isUsed()) {
                System.out.println("- " + e.getName() + " [" + e.getShortName() + "]");
            }
        });

        System.out.print("Enter equipment code to use (or 'cancel'): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        if (choice.equals("cancel")) return;

        Equipment equipment = equipmentBag.findResearchEquipment(choice);
        if (equipment == null) {
            throw new EquipmentException("Invalid equipment selection.");
        }

        equipment.use(lake, position.row, position.col);
        gameState.useEquipment(equipment);
        
        if (equipment instanceof ResearchEquipment) {
            ExperimentGoals.ExperimentType experimentType = equipment.getExperimentType();
            gameState.recordExperiment(researcher, experimentType);
            System.out.println("Successfully performed " + experimentType + " experiment!");
        }
    }

    private void handleHazard(String[][] lake, String researcher, Position position) {
        String hazard = lake[position.row][position.col];
        if (hazard.equals(CLIFF_EDGE)) {
            System.out.println(getResearcherName(researcher) + " fell into the cliff edge!");
            gameState.researcherFell(researcher);
        }
    }

    private Position findResearcher(String[][] lake, String researcher) {
        for (int i = 0; i < lake.length; i++) {
            for (int j = 0; j < lake[i].length; j++) {
                if (lake[i][j].equals(researcher)) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    private boolean isValidMove(String[][] lake, Position position) {
        return position.row >= 0 && position.row < rows && 
               position.col >= 0 && position.col < columns;
    }

    private boolean isHazard(String cellContent) {
        return cellContent.equals(CLIFF_EDGE) || 
               cellContent.equals(ICE_SPIKES) || 
               cellContent.equals(HOLE_IN_ICE) || 
               cellContent.equals(ICE_BLOCK);
    }

    private void showAvailableActions(EquipmentBag<Equipment> equipmentBag) {
        System.out.println("\nAvailable actions:");
        System.out.println("[M] Move");
        if (equipmentBag.hasUnusedEquipment()) {
            System.out.println("[E] Use equipment");
        }
        System.out.println("[F] Finish turn");
        System.out.println("[Q] Quit game");
        System.out.print("Choose action: ");
    }

    private String getResearcherName(String id) {
        return "Researcher " + id.substring(1);
    }

    private void printGameResults() {
        System.out.println("\n=== Game Over ===");
        System.out.println("Final Score: " + gameState.getScore());
        if (gameState.isAllExperimentsCompleted()) {
            System.out.println("All experiments completed successfully!");
        } else {
            System.out.println("Not all experiments were completed.");
        }
        System.out.println("Researchers who finished: " + gameState.getFinishedResearchers());
    }

    private static class Position {
        int row, col;
        
        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
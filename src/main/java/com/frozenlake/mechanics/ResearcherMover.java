package com.frozenlake.mechanics;

import java.util.*;
import com.frozenlake.exceptions.*;
import com.frozenlake.equipment.*;
import com.frozenlake.model.*;
import com.frozenlake.ui.LakePrinter;
import com.frozenlake.model.Direction;
import com.frozenlake.util.GameConstants;

public class ResearcherMover {
    private static final String EMPTY = GameConstants.EMPTY;
    private static final String ENTRANCE = GameConstants.ENTRANCE;
    private static final String CLIFF_EDGE = GameConstants.CLIFF_EDGE;
    private static final String ICE_BLOCK = GameConstants.ICE_BLOCK;
    private static final String ICE_SPIKES = GameConstants.ICE_SPIKES;
    private static final String HOLE_IN_ICE = GameConstants.HOLE_IN_ICE;
    private static final String BRIDGE = GameConstants.BRIDGE;
    private static final String CLIMBED = GameConstants.CLIMBED;

    private final int rows;
    private final int columns;
    private final EquipmentStorage equipmentStorage;
    private final GameState gameState;
    private final ExperimentPerformer experimentPerformer;
    private EquipmentBag<Equipment> currentEquipmentBag;

    public ResearcherMover(int rows, int columns, Queue<String> researchers, List<ExperimentGoals.ExperimentType> goals) throws GameException {
        if (rows <= 0 || columns <= 0) {
            throw new GameException("Invalid lake dimensions");
        }
        if (researchers == null || researchers.isEmpty()) {
            throw new GameException("Researchers queue cannot be null or empty");
        }
        if (goals == null || goals.isEmpty()) {
            throw new GameException("Experiment goals cannot be null or empty");
        }

        this.rows = rows;
        this.columns = columns;
        this.equipmentStorage = new EquipmentStorage();
        this.currentEquipmentBag = new EquipmentBag<>();
        
        // Initialize researchers at the entrance position (0, columns-1)
        Position entrancePosition = new Position(0, columns-1);
        Queue<Researcher> researcherQueue = new LinkedList<>();
        for (String r : researchers) {
            researcherQueue.offer(new Researcher(r, entrancePosition));
        }
        
        this.gameState = new GameState(goals, researcherQueue);
        this.experimentPerformer = new ExperimentPerformer();
    }

    public void move(Lake lake, Queue<String> researchers, LakePrinter lakePrinter, Scanner scanner) {
        while (!gameState.isGameOver() && !researchers.isEmpty()) {
            String currentResearcher = researchers.peek();
            if (currentResearcher == null) break;

            System.out.println("\n=====> " + getResearcherName(currentResearcher) + "'s turn");
            
            try {
                // Equipment selection phase
                currentEquipmentBag = selectEquipment(currentResearcher, scanner);
                
                // Movement phase
                boolean continueMoving = true;
                while (continueMoving && !gameState.isGameOver()) {
                    try {
                        lakePrinter.printLake(lake);
                        Position pos = findResearcher(lake, currentResearcher);
                        
                        if (pos == null) {
                            System.out.println("Researcher not found on the lake!");
                            break;
                        }

                        showAvailableActions(currentEquipmentBag);
                        String action = scanner.nextLine().trim().toLowerCase();

                        switch (action) {
                            case "m":
                                continueMoving = handleMovement(lake, currentResearcher, pos, scanner);
                                break;
                            case "e":
                                if (currentEquipmentBag.hasUnusedEquipment()) {
                                    handleEquipmentUsage(lake, currentResearcher, pos, currentEquipmentBag, scanner);
                                } else {
                                    System.out.println("No unused equipment available.");
                                }
                                break;
                            case "f":
                                continueMoving = false;
                                break;
                            case "q":
                                System.out.println("Game ended by user.");
                                return;
                            default:
                                System.out.println("Invalid action. Please try again.");
                        }
                    } catch (GameException e) {
                        System.out.println("Error: " + e.getMessage());
                        continueMoving = false;
                    }
                }

                // End of turn
                if (!gameState.isGameOver()) {
                    String removed = researchers.poll();
                    if (researchers.isEmpty() && !gameState.isAllExperimentsCompleted()) {
                        for (String r : gameState.getActiveResearchers()) {
                            researchers.offer(r);
                        }
                    }
                }
            } catch (GameException e) {
                System.out.println("Fatal error: " + e.getMessage());
                break;
            } finally {
                currentEquipmentBag = null;
            }
        }

        printGameResults();
    }

    private Position findResearcher(Lake lake, String researcher) throws GameException {
        if (lake == null || researcher == null) {
            throw new GameException("Invalid lake or researcher");
        }
        for (int i = 0; i < lake.getRows(); i++) {
            for (int j = 0; j < lake.getColumns(); j++) {
                Position pos = new Position(i, j);
                if (researcher.equals(lake.getCell(pos))) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean handleMovement(Lake lake, String researcher, Position position, Scanner scanner) 
            throws GameException {
        System.out.println("Choose direction ([U]p, [D]own, [L]eft, [R]ight): ");
        String direction = scanner.nextLine().trim().toLowerCase();

        if (!direction.matches("[udlr]")) {
            throw new MovementException("Invalid direction. Use U, D, L, or R.");
        }

        Position newPosition = calculateNewPosition(lake, position, direction);
        if (!isValidMove(lake, newPosition)) {
            throw new MovementException("Cannot move in that direction.");
        }

        try {
            lake.setCell(position, EMPTY);
            String cellContent = lake.getCell(newPosition);
            
            if (lake.isHazard(newPosition)) {
                if (cellContent.equals(CLIFF_EDGE)) {
                    handleHazard(lake, researcher, newPosition);
                    return false;
                }
                
                Equipment hazardEquipment = findHazardEquipment(currentEquipmentBag, cellContent);
                if (hazardEquipment != null) {
                    hazardEquipment.use(lake, newPosition);
                    gameState.useEquipment(hazardEquipment);
                    System.out.println("Used " + hazardEquipment.getName() + " to handle hazard!");
                } else {
                    System.out.println("No suitable equipment to handle this hazard!");
                    lake.setCell(position, researcher); // Move back
                    return false;
                }
            }
            
            lake.setCell(newPosition, researcher);
            return true;
        } catch (GameException e) {
            // Restore original position on error
            try {
                lake.setCell(position, researcher);
            } catch (GameException ex) {
                // If we can't restore position, throw original error
                throw e;
            }
            throw new MovementException("Error during movement: " + e.getMessage());
        }
    }

    private Position calculateNewPosition(Lake lake, Position start, String direction) throws GameException {
        if (lake == null || start == null) {
            throw new GameException("Invalid lake or position");
        }

        Direction dir;
        switch (direction) {
            case "u": dir = Direction.UP; break;
            case "d": dir = Direction.DOWN; break;
            case "l": dir = Direction.LEFT; break;
            case "r": dir = Direction.RIGHT; break;
            default: throw new MovementException("Invalid direction");
        }
        
        Position current = start;
        Position next = current.move(dir);
        
        while (next.isValid(rows, columns) && canMoveThrough(lake, next)) {
            current = next;
            next = current.move(dir);
        }
        
        return current;
    }

    private boolean canMoveThrough(Lake lake, Position pos) throws GameException {
        if (lake == null || pos == null) {
            return false;
        }
        String cell = lake.getCell(pos);
        return cell.equals(EMPTY) || cell.equals(BRIDGE) || cell.equals(CLIMBED) || cell.equals(CLIFF_EDGE);
    }

    private void handleEquipmentUsage(Lake lake, String researcher, Position position, 
            EquipmentBag<Equipment> equipmentBag, Scanner scanner) throws GameException {
        if (equipmentBag == null || !equipmentBag.hasUnusedEquipment()) {
            throw new GameException("No unused equipment available.");
        }

        System.out.println("\nAvailable equipment: ");
        for (Equipment e : equipmentBag.getItems()) {
            if (!e.isUsed()) {
                System.out.println("- " + e.getName() + " [" + e.getShortName() + "]");
            }
        }

        System.out.print("Enter equipment code to use (or 'cancel'): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        if (choice.equals("cancel")) return;

        Equipment equipment = equipmentBag.findResearchEquipment(choice);
        if (equipment == null) {
            throw new GameException("Invalid equipment selection.");
        }

        equipment.use(lake, position);
        gameState.useEquipment(equipment);
        
        if (equipment instanceof ResearchEquipment) {
            ExperimentGoals.ExperimentType experimentType = equipment.getExperimentType();
            gameState.recordExperiment(researcher, experimentType);
            System.out.println("Successfully performed " + experimentType + " experiment!");
        }
    }

    private void handleHazard(Lake lake, String researcher, Position position) throws GameException {
        String hazard = lake.getCell(position);
        if (hazard.equals(CLIFF_EDGE)) {
            System.out.println(getResearcherName(researcher) + " fell into the cliff edge!");
            gameState.researcherFell(researcher);
        }
    }

    private boolean isValidMove(Lake lake, Position position) throws GameException {
        if (lake == null || position == null) {
            throw new GameException("Invalid lake or position");
        }
        return position.isValid(lake.getRows(), lake.getColumns());
    }

    private Equipment findHazardEquipment(EquipmentBag<Equipment> equipmentBag, String hazardType) {
        if (equipmentBag == null || hazardType == null) {
            return null;
        }
        return equipmentBag.findEquipmentForHazard(hazardType);
    }

    private void showAvailableActions(EquipmentBag<Equipment> equipmentBag) {
        System.out.println("\nAvailable actions:");
        System.out.println("[M] Move");
        if (equipmentBag != null && equipmentBag.hasUnusedEquipment()) {
            System.out.println("[E] Use equipment");
        }
        System.out.println("[F] Finish turn");
        System.out.println("[Q] Quit game");
        System.out.print("Choose action: ");
    }

    private String getResearcherName(String id) {
        return id == null ? "Unknown" : "Researcher " + id.substring(1);
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

    private EquipmentBag<Equipment> selectEquipment(String researcher, Scanner scanner) throws GameException {
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
                System.out.print("Select equipment (or 'no' to finish): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("no")) {
                    equipmentBag.validateContents();
                    break;
                }

                Equipment equipment = equipmentStorage.getEquipment(choice);
                equipmentBag.addEquipment(equipment);
                System.out.println("Added " + equipment.getName() + " to bag.");
                System.out.println("Current bag: " + equipmentBag.getItems());

            } catch (GameException e) {
                System.out.println(e.getMessage());
            }
        }

        return equipmentBag;
    }
}
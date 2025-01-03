package com.frozenlake;

import java.util.*;
import com.frozenlake.mechanics.*;
import com.frozenlake.model.*;
import com.frozenlake.ui.LakePrinter;
import com.frozenlake.util.*;
import com.frozenlake.equipment.*;
import com.frozenlake.exceptions.*;

public class FrozenLakePuzzleApp {
    private static final int ROWS = 8;
    private static final int COLUMNS = 11;

    public static void main(String[] args) {
        GameMenu gameMenu = new GameMenu();
        gameMenu.start();
    }

    private static class GameMenu {
        private final Scanner scanner;
        private final Lake lake;
        private final Queue<Researcher> researchers;
        private final ExperimentGoals experimentGoals;
        private final GameState gameState;
        private final LakePrinter lakePrinter;
        private final EquipmentStorage equipmentStorage;

        public GameMenu() {
            this.scanner = new Scanner(System.in);
            LakeInitializer initializer = new LakeInitializer(ROWS, COLUMNS);
            this.lake = initializer.getLake();
            this.researchers = new LinkedList<>();
            this.experimentGoals = new ExperimentGoals();
            this.lakePrinter = new LakePrinter(ROWS, COLUMNS);
            this.equipmentStorage = new EquipmentStorage();
            
            // Create researchers
            createResearchers();
            
            // Generate experiment goals
            experimentGoals.generateExperimentGoals(researchers.size());
            
            // Initialize game state with researchers
            this.gameState = new GameState(experimentGoals.getExperimentGoals(), researchers);
        }

        public void start() {
            printWelcomeMessage();
            printExperimentGoals();
            lakePrinter.printLake(lake);

            while (!gameState.isGameOver()) {
                Researcher currentResearcher = researchers.peek();
                if (currentResearcher == null) {
                    break;
                }

                System.out.println("\n=====> " + currentResearcher.getDisplayName() + "'s turn");
                handleResearcherTurn(currentResearcher);

                if (gameState.isGameOver()) {
                    printGameResults();
                    break;
                }
            }

            scanner.close();
        }

        private void createResearchers() {
            // Create 2-4 researchers randomly
            int numResearchers = new Random().nextInt(3) + 2;
            for (int i = 1; i <= numResearchers; i++) {
                researchers.offer(new Researcher("R" + i, new Position(0, 0)));
            }
        }

        private void printWelcomeMessage() {
            System.out.println("Welcome to Frozen Lake Puzzle App. There are " + 
                researchers.size() + " researchers waiting at the lake entrance.");
        }

        private void printExperimentGoals() {
            System.out.println("\nThere are " + experimentGoals.getExperimentGoals().size() + 
                " experiment(s) that must be completed:");
            for (ExperimentGoals.ExperimentType goal : experimentGoals.getExperimentGoals()) {
                System.out.println("- " + goal.toString());
            }
        }

        private void handleResearcherTurn(Researcher researcher) {
            try {
                // Equipment selection phase
                handleEquipmentSelection(researcher);

                // Movement phase
                boolean continueMoving = true;
                while (continueMoving && !gameState.isGameOver()) {
                    lakePrinter.printLake(lake);
                    showAvailableActions(researcher);
                    
                    String action = getValidInput("[1-3]", "Choose action: ").toLowerCase();
                    try {
                        switch (action) {
                            case "1":
                                continueMoving = handleMovement(researcher);
                                break;
                            case "2":
                                handleExperiment(researcher);
                                break;
                            case "3":
                                continueMoving = false;
                                break;
                            default:
                                System.out.println("Invalid action. Please try again.");
                        }
                    } catch (GameException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                // End of turn
                if (!gameState.isGameOver()) {
                    researchers.poll();
                    if (researchers.isEmpty() && !gameState.isAllExperimentsCompleted()) {
                        // Reset queue if experiments not completed
                        for (String researcherId : gameState.getActiveResearchers()) {
                            researchers.offer(new Researcher(researcherId, new Position(0, 0)));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        private void handleEquipmentSelection(Researcher researcher) {
            EquipmentBag<Equipment> bag = new EquipmentBag<>();
            
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
                    String choice = getValidInput("[td|ws|cm|ch|cl|wb|no]", 
                        "Select equipment (or 'no' to finish): ").toLowerCase();

                    if (choice.equals("no")) {
                        bag.validateContents();
                        break;
                    }

                    Equipment equipment = equipmentStorage.getEquipment(choice);
                    bag.addEquipment(equipment);
                    System.out.println("Added " + equipment.getName() + " to bag.");
                    System.out.println("Current bag: " + bag.getItems());

                } catch (GameException e) {
                    System.out.println(e.getMessage());
                }
            }

            researcher.setEquipmentBag(bag);
        }

        private boolean handleMovement(Researcher researcher) throws GameException {
            String direction = getValidInput("[udlr]", 
                "Choose direction ([U]p, [D]own, [L]eft, [R]ight): ").toLowerCase();

            Direction dir = Direction.fromCode(direction);
            if (dir == null) {
                throw new MovementException("Invalid direction");
            }

            Position newPosition = calculateNewPosition(researcher.getPosition(), dir);
            if (isValidMove(newPosition)) {
                moveResearcher(researcher, newPosition);
                return true;
            }
            
            throw new MovementException("Cannot move in that direction");
        }

        private void handleExperiment(Researcher researcher) throws GameException {
            if (!researcher.getEquipmentBag().isResearchBag()) {
                throw new UnavailableEquipmentException("No research equipment available");
            }

            String equipmentCode = getValidInput("[td|ws|cm|ch]", 
                "Enter the name of the research equipment: ").toLowerCase();

            Equipment equipment = researcher.getEquipmentBag().findResearchEquipment(equipmentCode);
            equipment.use(lake, researcher.getPosition());
            gameState.useEquipment(equipment);
            
            if (equipment instanceof ResearchEquipment) {
                ResearchEquipment researchEquip = (ResearchEquipment) equipment;
                System.out.println(researchEquip.getFormattedResult());
                gameState.recordExperiment(researcher.getId(), researchEquip.getExperimentType());
            }
        }

        private void showAvailableActions(Researcher researcher) {
            System.out.println("\nAvailable actions:");
            System.out.println("[1] Continue moving on the ice");
            if (researcher.hasUnusedEquipment()) {
                System.out.println("[2] Choose experiment equipment and perform an experiment");
            }
            System.out.println("[3] Sit on the ground and let the other researchers head out to the lake");
        }

        private String getValidInput(String pattern, String prompt) {
            while (true) {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.toLowerCase().matches(pattern)) {
                    return input;
                }
                System.out.println("Invalid input. Please try again.");
            }
        }

        private Position calculateNewPosition(Position start, Direction direction) {
            // Implementation of sliding movement
            Position current = start;
            while (canMoveThrough(current.move(direction))) {
                current = current.move(direction);
            }
            return current;
        }

        private boolean isValidMove(Position position) {
            return position.isValid(ROWS, COLUMNS);
        }

        private boolean canMoveThrough(Position position) {
            if (!position.isValid(ROWS, COLUMNS)) {
                return false;
            }
            try {
                return lake.canMoveThrough(position);
            } catch (GameException e) {
                return false;
            }
        }

        private void moveResearcher(Researcher researcher, Position newPosition) throws GameException {
            if (researcher == null || newPosition == null) {
                throw new GameException("Researcher and position cannot be null");
            }

            Position oldPosition = researcher.getPosition();
            lake.moveResearcher(researcher.getId(), oldPosition, newPosition);
            researcher.setPosition(newPosition);

            // Handle hazards
            if (lake.isHazard(newPosition)) {
                handleHazard(researcher, newPosition);
            }
        }

        private void handleHazard(Researcher researcher, Position position) throws GameException {
            if (researcher == null || position == null) {
                throw new GameException("Researcher and position cannot be null");
            }

            String hazard = lake.getCell(position);
            Equipment hazardEquipment = researcher.getEquipmentBag().findEquipmentForHazard(hazard);

            if (hazardEquipment != null) {
                hazardEquipment.use(lake, position);
                gameState.useEquipment(hazardEquipment);
                System.out.println("Used " + hazardEquipment.getName() + " to handle hazard!");
            } else if (hazard.equals(GameConstants.CLIFF_EDGE)) {
                System.out.println(researcher.getDisplayName() + " fell into the cliff edge!");
                gameState.researcherFell(researcher.getId());
            }
        }

        private void printGameResults() {
            System.out.println("\n=== Game Over ===");
            System.out.println("Final Score: " + gameState.getScore());
            
            if (gameState.isAllExperimentsCompleted()) {
                System.out.println("All experiments completed successfully!");
                System.out.println("\nExperiment Results:");
                // Print experiment results
                for (Researcher researcher : researchers) {
                    Set<ExperimentGoals.ExperimentType> completed = 
                        gameState.getCompletedExperiments(researcher.getId());
                    if (!completed.isEmpty()) {
                        System.out.println("\n" + researcher.getDisplayName() + "'s experiments:");
                        completed.forEach(type -> System.out.println("- " + type));
                    }
                }
            } else {
                System.out.println("Not all experiments were completed.");
            }
            
            if (!gameState.getFinishedResearchers().isEmpty()) {
                System.out.println("\nResearchers who finished: " + 
                    gameState.getFinishedResearchers());
            }
        }
    }
}

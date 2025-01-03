package FrozenLakePuzzle;

import java.util.Random;

public class HoleInIcePlacer {
    private static final String EMPTY = "  "; // Empty space
    private static final String HOLE_IN_ICE = "HI"; // Hole in Ice
    private int ROWS;
    private int COLUMNS;

    // Constructor name fixed to match the class name
    public HoleInIcePlacer(int rows, int columns) {
        this.ROWS = rows;
        this.COLUMNS = columns;
    }

    public void placeRandomItems(String[][] lake, int count) {
        Random random = new Random();
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLUMNS);
            if (lake[row][col].equals(EMPTY)) {
                lake[row][col] = HOLE_IN_ICE;
                placed++;
            }
        }
    }
}
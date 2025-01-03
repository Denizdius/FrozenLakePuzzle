package FrozenLakePuzzle;
import java.util.Random;

public class IceBlockPlacer {
    private static final String ICE_BLOCK = "IB"; // Ice Block
    private static final String CLIFF_EDGE = "CE"; // Cliff Edge
    private static final String EMPTY = "  "; // Empty space
    private static final String ENTRANCE = "E"; // Entrance
    private int ROWS;
    private int COLUMNS;

    public IceBlockPlacer(int rows, int columns) {
        this.ROWS = rows;
        this.COLUMNS = columns;
    }

    public void placeIceBlocks(String[][] lake, int iceBlockCount) {
        Random random = new Random();
        //System.out.println(iceBlockCount);
        int placed = 0;
        //System.out.println();
        int IB_column;
        boolean columnSixAssigned = false; // Flag to check if column 6 has been used

        while (placed < iceBlockCount) {

            int entrance_column = 0;
            for (int i = 0; i < lake.length; i++) {
                for (int j = 0; j < lake[i].length; j++) {
                    if (lake[i][j] != null && lake[i][j].trim().equals(ENTRANCE)) {
                        entrance_column = j;
                        //System.out.println("ENTRANCE placed at coordinates: (" + i + ", " + j + ")");
                        break;
                    }
                }
            }

            //System.out.println("Entrance column: " + entrance_column);

            for (int i = 0; i < ROWS; i++) {
                IB_column = random.nextInt(COLUMNS);
                // System.out.println(IB_column);
                // lake[i][IB_column] = ICE_BLOCK;

                if (lake[i][IB_column] == CLIFF_EDGE) {
                    // System.out.println("cs:"+IB_column);
                    while (lake[i][IB_column] == CLIFF_EDGE) {
                        IB_column = random.nextInt(COLUMNS);
                        //System.out.println("new1:" + IB_column);
                    }

                } else if (IB_column == entrance_column && i == 0) {
                    //System.out.println(IB_column);
                    while (IB_column == entrance_column && i == 0) {
                        IB_column = random.nextInt(COLUMNS);
                        //System.out.println("new2:" + IB_column);
                    }
                }

                if (lake[i][IB_column].equals(EMPTY)) {
                    lake[i][IB_column] = ICE_BLOCK;
                    placed++;
                    // System.out.println(placed);
                }
                if (IB_column == entrance_column) {
                    columnSixAssigned = true;
                }

            }
            // System.out.println(columnSixAssigned);

            if (!columnSixAssigned) {

                Random random2 = new Random();
                int new_row = random2.nextInt(ROWS - 1) + 1; // Ensure the new row is valid (1 to ROWS-1)
                // System.out.println("NEW ROW= " + new_row);
                IB_column = entrance_column; // Force column to be 6
                columnSixAssigned = true; // Mark as assigned
                lake[new_row][IB_column] = ICE_BLOCK;

                // Remove the old ICE_BLOCK
                outerLoop: for (int row = 0; row < ROWS; row++) {
                    for (int col = 0; col < COLUMNS; col++) {
                        if (lake[new_row][col] == ICE_BLOCK && col != entrance_column) { // Compare using .equals()
                            //System.out.println("Removing ICE_BLOCK from row " + new_row + ", column " + col);
                            lake[new_row][col] = EMPTY; // Set to EMPTY
                            //System.out.println("Old block cleared.");
                            break outerLoop; // Exit both loops once the old block is cleared
                        }
                    }
                }

                // System.out.println("NEW ROW= " + new_row);
                // System.out.println("New column: " + IB_column);

            }

        }
    }
}
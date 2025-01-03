package FrozenLakePuzzle;
public class LakePrinter {
    private int rows;
    private int columns;

    public LakePrinter(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public void printLake(String[][] lake) {

        // Create walls (top boundary)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + 1; j++) {
                if ("E".equals(lake[i][j])) {
                    System.out.print("    ");
                } else if (i == 0) {
                    System.out.print("---");
                }
            }
        }
        System.out.println();

        // Check if the entire lake is empty
        boolean isColumnEmpty = true;
        for (String[] column : lake) {
            for (String cell : column) {
                if (cell != null) {
                    isColumnEmpty = false;
                    break;
                }
            }
        }

        // Print the lake content
        for (String[] row : lake) {
            boolean isRowEmpty = true;
            for (String cell : row) {
                if (cell != null) {
                    isRowEmpty = false;
                    break;
                }
            }

            // Skip completely empty rows or columns
            if (isRowEmpty || isColumnEmpty) {
                continue;
            }

            System.out.print("|");
            for (String cell : row) {
                if (cell != null) {
                    System.out.print(cell + "|");
                }
            }
            System.out.println();
        }

        // Create walls (bottom boundary)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns + 1; j++) {
                if (i == rows - 1) {
                    System.out.print("---");
                }
            }
        }
        System.out.println();
    }
}
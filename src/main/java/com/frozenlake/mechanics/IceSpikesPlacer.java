package FrozenLakePuzzle;

import java.util.Random;

public class IceSpikesPlacer {
    private static final String EMPTY = "  "; // Empty space
    private static final String ICE_SPIKES = "IS"; // Ice Spikes
    private int ROWS;
    private int COLUMNS;

    public IceSpikesPlacer(int rows, int columns) {
        this.ROWS = rows;
        this.COLUMNS = columns;
    }

    public void placeIceSpikes(String[][] lake, int cliffEdgePosition, int count) {
        Random random;
        int placed = 0;

        while (placed < count) {
            random = new Random();
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLUMNS);

            // Ensure IceSpikes are placed near walls, and not on the cliff edge or entrance
            boolean isValidPlacement = false;

            switch (cliffEdgePosition) {
                case 0: // Cliff edge on the first column
                    isValidPlacement = (row==0 || row==7 || col==12) && lake[row][col].equals(EMPTY) && !((row==0 && col==1) && !(row==7 && col==1) )  && !((2<col && row==0) && (col < 10 && row==0)); 
                    //isValidPlacement = col > 0&& lake[row][col].equals(EMPTY);
                    break;
                case 1: // Cliff edge on the last column
                    isValidPlacement = (col==0 || row==7 || row==0) && lake[row][col].equals(EMPTY) && !((row==0 && col==11) || (row==7 && col==11)); 
                    break;
                default: // Cliff edge on the last row
                    isValidPlacement = (col==0 || col==12 || row==0) && lake[row][col].equals(EMPTY) && !((row==7 && col==0) || (row==7 && col==10));
                    break;
            }

            if (isValidPlacement) {
                lake[row][col] = ICE_SPIKES;
                placed++;
            }
        }
    }
    
}
import java.util.*;
import java.io.*;
import java.awt.*;

public class MapDataDrawer {

    private int[][] grid;
    int rows;
    int columns;
    int minValue;
    int maxValue;
    boolean displayIsOn;    // Enable/disable drawing

    // Constructor
    public MapDataDrawer(String fileName) {
        Scanner reader = null;         // create a reader
        try {
            reader = new Scanner( new FileInputStream( fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read the first two numbers in the file, which should respectively be the number of rows and columns
        rows = reader.nextInt();
        columns = reader.nextInt();

        // initialize grid
        grid = new int[rows][columns];

        //read the data from the file into the grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[ i][j] = reader.nextInt();
            }
        }

        minValue = findMinValueInGrid();
        maxValue = findMaxValueInGrid();

        displayIsOn = true;     // default is drawing is enabled.

        System.out.println("Successfully read file " + fileName);
    }//end MapDataDrawer()

    /**
     * @return the min value in the entire grid
     */
    public int findMinValueInGrid() {
        minValue = Integer.MAX_VALUE;

        // Step through all array elements
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                if( grid[i][j] < minValue) {
                    minValue = grid[i][j];
                }

            }//end for( int j
        }//end for( int i

        return minValue;
    }//end findMinValueInGrid()

    /**
     * @return the max value in the entire grid
     */
    public int findMaxValueInGrid() {
        maxValue = Integer.MIN_VALUE;

        // Step through all array elements
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                if( grid[i][j] > maxValue) {
                    maxValue = grid[i][j];
                }

            }//end for( int j
        }//end for( int i

        return maxValue;
    }//end findMaxValueInGrid()

    /**
     * @param col the column of the grid to check
     * @return the index of the row with the lowest value in the given col for the grid
     */
    public int findIndexOfMinimumValueInColumn(int col) {
        int minimumInColValue = Integer.MAX_VALUE;
        int indexOfMinimumValue = 0;

        // Step through all row elements for this column
        for (int i = 0; i < rows; i++) {
            if( grid[i][col] < minimumInColValue) {
                minimumInColValue = grid[i][col];
                indexOfMinimumValue = i;
            }
        }//end for( int j

        return indexOfMinimumValue;
    }//end findIndexOfMinimumValueInColumn()


    /**
     * Draws the grid using the given Graphics object.
     * Colors should be grayscale values 0-255, scaled based on min/max values in grid
     */
    public void drawMap(Graphics g) {
        // Since we have the minValue and the maxValue, compute the scale factor
        double scaleFactor = 255.0 / (maxValue - minValue);

        // Draw each small rectangle
        // Step through all array elements
        for (int i = 0; i < rows; i++) {
            if( i==1) {
                System.out.println("Pause.");
            }
            for (int j = 0; j < columns; j++) {

                int c = (int)( (grid[i][j] - minValue) * scaleFactor);   //calculated grayscale value
                // Make negative values blue (water)
                if( grid[i][j] > 0) {
                    g.setColor(new Color(c, c, c));
                }
                else {
                    g.setColor(new Color(c, c, 255));
                }
                // Our data is stored row,col, but the graphics drawing expects col,row, so reverse them here
                if( displayIsOn) {
                    g.fillRect(j,i,1,1);
                }

            }//end for( int j
        }//end for( int i
    }//end drawMap()


    // Find the smallest elevation change of the 3 neighbors to the right and return it.
    // If there is a tie, choose one at random.
    private int findRowWithSmallestChange( int x, int y, int z,
                                           Random randGenerator)  // random number generator that has had seed applied
    {
        int returnValue = -1;

        if( x<y && x<z)
            returnValue = -1;  // new row is up one
        else if( y<x && y<z)
            returnValue = 0;   // new row is the same
        else if( z<x && z<y)
            returnValue = 1;   // new row is down one
        else {
            // There is a tie between at least two of them, so choose new row change (-1,0,+1) at random
            if( x==y)
                returnValue = (randGenerator.nextDouble() > 0.5) ? -1 : 0;
            else if( x==z)
                returnValue = (randGenerator.nextDouble()  > 0.5) ? -1 : 1;
            else if( y==z)
                returnValue = (randGenerator.nextDouble()  > 0.5) ? 0 : 1;
            else {
                // Sanity check.  Should never get here
                System.out.println("Logic error in finding row with smallest change.  Exiting program.");
                System.exit( -1);
            }
        }

        return returnValue;
    }//end findRowWithSmallestChange()


    /**
     * Find a path from West-to-East starting at given row.
     * Choose a foward step out of 3 possible forward locations, using greedy method described in assignment.
     *
     * @return the total change in elevation traveled from West-to-East
     */
    public int drawGreedyPathStartingAt(Graphics g, int row, int column) {
        int totalVerticalChange = 0;
        Random randGenerator = new Random( 1);  // Seed the random number generator, for predictable results

        // On each iteration make a step either directly right, or right-and-up or right-and-down
        while( column < (columns-1)) {
            // Ensure row is always at least 1 away from the upper boundary (0) and the lower, so that
            // checking the neighbors always works
            if( row == 0) row++;          // change it to 1 to avoid arrray references attempting to go before the top row
            if( row == (rows-1)) row--;   // decrement by one to avoid array references going past the last row

            // Since our data is in row,col order but graphics are in col,row order, swap them when graphing
            if( displayIsOn) {
                g.fillRect(column, row,1,1);
            }

            // Find new location.  New row will either be the same (0), up (-1) or down (+1)
            int rowChange = findRowWithSmallestChange(
                                    Math.abs(grid[row][column] - grid[ row-1][column+1]),   // right and up one
                                    Math.abs(grid[row][column] - grid[ row  ][column+1]),   // straight across to the right
                                    Math.abs(grid[row][column] - grid[ row+1][column+1]),   // right and down one
                                    randGenerator);             // random number generator, that has seed applied
            // Add to the total Vertical Change so far
            totalVerticalChange += Math.abs( grid[row][column] - grid[row + rowChange][column+1] );
            row += rowChange;
            column++;   // Advance to next column
        }

        // Graph the last (right-most) column
        // Since our data is in row,col order but graphics are in col,row order, swap them when graphing
        if( displayIsOn) {
            g.fillRect(column, row,1,1);
        }

        return totalVerticalChange;
    }//end drawGreedyPathStartingAt()


    /**
     * Find a REVERSE path from East-to-West starting at given row and column.
     * Choose a foward step out of 3 possible forward locations, using greedy method described in assignment.
     *
     * @return the total change in elevation traveled in REVERSE, from East-to-Wests
     */
    public int drawGreedyREVERSEPathStartingAt(Graphics g, int row, int column) {
        int totalVerticalChange = 0;
        Random randGenerator = new Random( 1);  // Seed the random number generator, for predictable results

        // On each iteration make a step either directly left, or left-and-up or left-and-down
        while( column > 0) {
            // Ensure row is always at least 1 away from the upper boundary (0) and the lower, so that
            // checking the neighbors always works
            if( row == 0) row++;          // change it to 1 to avoid arrray references attempting to go before the top row
            if( row == (rows-1)) row--;   // decrement by one to avoid array references going past the last row

            // Since our data is in row,col order but graphics are in col,row order, swap them when graphing
            if( displayIsOn) {
                g.fillRect(column, row, 1, 1);
            }

            // Find new location.  New row will either be the same (0), up (-1) or down (+1)
            int rowChange = findRowWithSmallestChange(
                    Math.abs(grid[row][column] - grid[ row-1][column-1]),   // left and up one
                    Math.abs(grid[row][column] - grid[ row  ][column-1]),   // straight across to the left
                    Math.abs(grid[row][column] - grid[ row+1][column-1]),   // left and down one
                    randGenerator);             // random number generator, that has seed applied
            // Add to the total Vertical Change so far
            totalVerticalChange += Math.abs( grid[row][column] - grid[row + rowChange][column-1] );
            row += rowChange;
            column--;   // Advance to next column
        }

        // Graph the last (left-most) column
        // Since our data is in row,col order but graphics are in col,row order, swap them when graphing
        if( displayIsOn) {
            g.fillRect(column, row,1,1);
        }

        return totalVerticalChange;
    }//end drawGreedyREVERSEPathStartingAt()


    /**
     * Draw lowest row in each column, as a hint for future heuristics
     */
    public void drawLowestElevationInEachColumn(Graphics g) {
        int lowestValue = 0;
        int lowestValueRow = 0;

        // for each column
        for (int col = 0; col < columns; col++) {
            lowestValue = Integer.MAX_VALUE;
            lowestValueRow = -1;

            // for each row
            for( int row = 0; row < rows; row++) {
                if (grid[row][col] < lowestValue) {
                    lowestValue = grid[row][col];
                    lowestValueRow = row;
                }
            }

            // We now have the lowest elevation in this column, so graph it.
            // Since our data is in row,col order but graphics are in col,row order, swap them when graphing
            if( displayIsOn) {
                g.fillRect(col, lowestValueRow, 1, 1);
            }
        }
    }//end drawLowestElevationInEachColumn()


    /**
     * @return the index of the starting row for the lowest-elevation-change path in the entire grid.
     */
    public int indexOfLowestElevPath(Graphics g) {
        return( findIndexOfMinimumValueInColumn( 0));
    }

    // "Get" methods
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return columns;
    }

    // "Set" methods
    public void setDisplayIsOnTo( boolean value) { displayIsOn = value; }
}//end class MapDataDrawer